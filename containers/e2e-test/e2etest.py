import json
from minio import Minio
import pika
import os
from pymongo import MongoClient
import time
import sys

RABBITMQ_ENDPOINT = os.environ['RABBITMQ_ENDPOINT']
RABBITMQ_USER = os.environ['RABBITMQ_USER']
RABBITMQ_PASS = os.environ['RABBITMQ_PASS']
RABBITMQ_VHOST = os.environ['RABBITMQ_VHOST']
RABBITMQ_QUEUE_DOWNLOADCOMPLETED = os.environ['RABBITMQ_QUEUE_DOWNLOADCOMPLETED']

MINIO_NODE_ENDPOINT = os.environ['MINIO_NODE_ENDPOINT']
MINIO_NODE_USER = os.environ['MINIO_NODE_USER']
MINIO_NODE_PASS = os.environ['MINIO_NODE_PASS']
MINIO_NODE_BUCKET = os.environ['MINIO_NODE_BUCKET']

MINIO_INTERNAL_ENDPOINT = os.environ['MINIO_INTERNAL_ENDPOINT']
MINIO_INTERNAL_USER = os.environ['MINIO_INTERNAL_USER']
MINIO_INTERNAL_PASS = os.environ['MINIO_INTERNAL_PASS']
MINIO_INTERNAL_BUCKET = os.environ['MINIO_INTERNAL_BUCKET']

MONGODB_ENDPOINT = os.environ['MONGODB_ENDPOINT']
MONGODB_DATABASE = os.environ['MONGODB_DATABASE']


def initDatabase():

    database.SongDownload.delete_many({})
    database.StorageNode.delete_many({})

    #Create SongDownload
    song_download = {
        "downloadId" : "7f596b76",
        "songName" : "testSong",
        "status" : "DOWNLOADING",
        "stored" : False,
        "userId" : "TEST_USER_ID"
    }
    database.SongDownload.insert_one(song_download)

    storage_node = {
        "name" : "okteto-minio-test",
        "stable" : True,
        "bucket" : MINIO_NODE_BUCKET,
        "type" : "MINIO",
        "endpoint" : MINIO_NODE_ENDPOINT,
        "credUser" : MINIO_NODE_USER,
        "credPass" : MINIO_NODE_PASS
    }
    database.StorageNode.insert_one(storage_node)

def storeInitialSongInternal():
    minio_client_internal.fput_object(
        MINIO_INTERNAL_BUCKET,
        "7f596b76.mp3",
        "./7f596b76.mp3"
    )


#Create minio node client
minio_client_node = Minio(
    MINIO_NODE_ENDPOINT,
    access_key=MINIO_NODE_USER,
    secret_key=MINIO_NODE_PASS,
    #secure=False
)

#Create minio internal client
minio_client_internal = Minio(
    MINIO_INTERNAL_ENDPOINT,
    access_key=MINIO_INTERNAL_USER,
    secret_key=MINIO_INTERNAL_PASS,
    #secure=False
)

#Create RabbitMQ client
connection = pika.BlockingConnection(
    pika.ConnectionParameters(
        host=RABBITMQ_ENDPOINT,
        virtual_host=RABBITMQ_VHOST,
        credentials=pika.PlainCredentials(RABBITMQ_USER, RABBITMQ_PASS)
    )
)
channel = connection.channel()

#Create MongoDB client
mongo_client = MongoClient(
    host=MONGODB_ENDPOINT
)
database = mongo_client[MONGODB_DATABASE]


#Prepare tests

initDatabase()
storeInitialSongInternal()


#Do tests

#Queue MUST be declared by storagemanager
channel.basic_publish(
    exchange=RABBITMQ_QUEUE_DOWNLOADCOMPLETED,
    routing_key='',
    body='{"downloadId" : "7f596b76"}'
)

print("Test message sent")


#Loop until 2 minutes searching for the object uploaded

timeout = time.time() + 60*2
error = False
while True:
    try:
        res = minio_client_node.stat_object(
            MINIO_NODE_BUCKET,
            "testSong.mp3"
        )
        error = False
        break
    except Exception as e:
        if e.message == "Object does not exist":
            pass
        else:
            sys.exit("Error checking object node existance: " +  e.message)
    
    if time.time() > timeout:
        error = True
        break

if error:
    sys.exit("Test file not found in storage node. Test failed")
else:
    print("E2E Test passed")
    minio_client_node.remove_object(
        MINIO_NODE_BUCKET,
        "testSong.mp3"
    )