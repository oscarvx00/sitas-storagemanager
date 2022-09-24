
pipeline {
    agent {label '!master'}
    stages {
        stage ('Checkout') {
            steps {
                dir('sources'){
                    git url: 'https://github.com/oscarvx00/sitas-storagemanager', branch: 'main'
                }
            }
        }
        stage ('Build'){
            environment {
                JAVA_HOME='/usr/lib/jvm/default-jvm'
            }
            steps {
                dir('build-image'){
                    sh 'cp -r -a ../sources/. ./'
                    sh 'cp -r -a containers/build/. ./'
                    script {
                        docker.build("sitas-storagemanager-buildcontainer").inside{
                            sh './gradlew jar' //We have to set JAVA_HOME because it takes the env from agent container
                            dir('build/libs'){
                                //stash includes: '*.jar', name: 'jarFile'
                                //archiveArtifacts artifacts: '*.jar'
                            }
                        }
                    }
                }
            }
        }

        //Test stage runs in slave container, cant launch sonarqube from test container
        stage ('Test'){
            environment {
                scannerHome = tool 'SonarQubeScanner'
                MONGODB_ENDPOINT = credentials("MONGODB_ENDPOINT")
                MONGODB_DATABASE = "sitas-test"
                MONGODB_DATABASE_PROD="sitas-prod"
                //MINIO_INTERNAL_ENDPOINT = ${MINIO_INTERNAL_ENDPOINT}
                MINIO_INTERNAL_USER = credentials("MINIO_INTERNAL_USER")
                MINIO_INTERNAL_PASS = credentials("MINIO_INTERNAL_PASS")
                MINIO_INTERNAL_BUCKET = "internal-storage-test"
                //RABBITMQ_ENDPOINT = ${RABBITMQ_ENDPOINT}
                RABBITMQ_USER = credentials("RABBITMQ_USER")
                RABBITMQ_PASS = credentials("RABBITMQ_PASS")
                //RABBITMQ_VHOST = ${RABBITMQ_VHOST}
                RABBITMQ_QUEUE_DOWNLOADCOMPLETED = "sitas-test-queue-downloadcompleted"
                MINIO_NODE_ENDPOINT = "https://minio-oscarvx00.cloud.okteto.net"
                MINIO_NODE_USER = credentials("MINIO_INTERNAL_USER")
                MINIO_NODE_PASS = credentials("MINIO_INTERNAL_PASS")
                MINIO_NODE_BUCKET = "node-storage-test"
            }
            steps {
                dir('sources'){
                    script {
                        sh './gradlew test'
                        sh './gradlew integrationTest'
                        sh './gradlew jacocoTestReport'
                    }
                    withSonarQubeEnv('sonarqube'){
                        sh './gradlew sonarqube --stacktrace'
                    }
                    script {
                        def qualitygate = waitForQualityGate()
                        if(qualitygate.status != 'OK'){
                            error "Pipeline aborted due to quality gate coverage failure."
                        }
                    }
                }
            }
        }
        stage ('E2E Test'){
            environment {
                MONGODB_ENDPOINT = credentials("MONGODB_ENDPOINT")
                MONGODB_DATABASE = "sitas-test"
                //MINIO_INTERNAL_ENDPOINT = ${MINIO_INTERNAL_ENDPOINT}
                MINIO_INTERNAL_USER = credentials("MINIO_INTERNAL_USER")
                MINIO_INTERNAL_PASS = credentials("MINIO_INTERNAL_PASS")
                MINIO_INTERNAL_BUCKET = "internal-storage-test"
                //RABBITMQ_ENDPOINT = ${RABBITMQ_ENDPOINT}
                RABBITMQ_USER = credentials("RABBITMQ_USER")
                RABBITMQ_PASS = credentials("RABBITMQ_PASS")
                //RABBITMQ_VHOST = ${RABBITMQ_VHOST}
                RABBITMQ_QUEUE_DOWNLOADCOMPLETED = "sitas-test-queue-downloadcompleted"
                MINIO_NODE_ENDPOINT = "minio-oscarvx00.cloud.okteto.net"
                MINIO_NODE_USER = credentials("MINIO_INTERNAL_USER")
                MINIO_NODE_PASS = credentials("MINIO_INTERNAL_PASS")
                MINIO_NODE_BUCKET = "node-storage-test"
                JAVA_HOME='/usr/lib/jvm/default-jvm'
            }
            steps {
                dir('e2e'){
                    sh 'cp -r -a ../sources/. ./'
                    sh 'cp -r -a containers/e2e-test/. ./'
                    sh """
                    docker build \
                        --build-arg MONGODB_ENDPOINT=mongodb+srv://sitas-db-user:mSudF19AlNNR510G@sitas-cluster0.3byxaum.mongodb.net/?retryWrites=true \
                        --build-arg MONGODB_DATABASE=sitas-test \
                        --build-arg MINIO_INTERNAL_ENDPOINT=minio-oscarvx00.cloud.okteto.net \
                        --build-arg MINIO_INTERNAL_USER=myaccesskey \
                        --build-arg MINIO_INTERNAL_PASS=mysecretkey \
                        --build-arg MINIO_INTERNAL_BUCKET=internal-storage-test \
                        --build-arg RABBITMQ_ENDPOINT=goose-01.rmq2.cloudamqp.com \
                        --build-arg RABBITMQ_USER=oaoesvtq \
                        --build-arg RABBITMQ_PASS=nnyfgti9CbBnS4-6Oq6iSWMncUhscG5d \
                        --build-arg RABBITMQ_VHOST=oaoesvtq \
                        --build-arg RABBITMQ_QUEUE_DOWNLOADCOMPLETED=sitas-test-queue-downloadcompleted \
                        --build-arg MINIO_NODE_ENDPOINT=minio-oscarvx00.cloud.okteto.net \
                        --build-arg MINIO_NODE_USER=myaccesskey \
                        --build-arg MINIO_NODE_PASS=mysecretkey \
                        --build-arg MINIO_NODE_BUCKET=node-storage-test \
                        -t oscarvicente/sitas-storagemanager-e2e  .
                    """

                    //Run container
                    sh script: "docker run sitas-storagemanager-e2e"

                }
            }
        }
        stage ('Deploy') {
            environment {
                MONGODB_ENDPOINT = credentials("MONGODB_ENDPOINT")
                MONGODB_DATABASE = "sitas-prod"
                //MINIO_INTERNAL_ENDPOINT = ${MINIO_INTERNAL_ENDPOINT}
                MINIO_INTERNAL_USER = credentials("MINIO_INTERNAL_USER")
                MINIO_INTERNAL_PASS = credentials("MINIO_INTERNAL_PASS")
                MINIO_INTERNAL_BUCKET = "internal-storage-prod"
                //RABBITMQ_ENDPOINT = ${RABBITMQ_ENDPOINT}
                RABBITMQ_USER = credentials("RABBITMQ_USER")
                RABBITMQ_PASS = credentials("RABBITMQ_PASS")
                //RABBITMQ_VHOST = ${RABBITMQ_VHOST}
                RABBITMQ_QUEUE_DOWNLOADCOMPLETED = "sitas-prod-queue-downloadcompleted"
                MINIO_NODE_ENDPOINT = "minio-oscarvx00.cloud.okteto.net"
                MINIO_NODE_USER = credentials("MINIO_INTERNAL_USER")
                MINIO_NODE_PASS = credentials("MINIO_INTERNAL_PASS")
                MINIO_NODE_BUCKET = "node-storage-prod"
                JAVA_HOME='/usr/lib/jvm/default-jvm'
            }
            steps {
                dir('deploy') {
                    sh 'cp -r -a ../sources/. ./'
                    sh 'cp -r -a containers/prod/. ./'

                    sh """
                    docker build \
                        --build-arg MONGODB_ENDPOINT=mongodb+srv://sitas-db-user:mSudF19AlNNR510G@sitas-cluster0.3byxaum.mongodb.net/?retryWrites=true \
                        --build-arg MONGODB_DATABASE=sitas-test \
                        --build-arg MINIO_INTERNAL_ENDPOINT=minio-oscarvx00.cloud.okteto.net \
                        --build-arg MINIO_INTERNAL_USER=myaccesskey \
                        --build-arg MINIO_INTERNAL_PASS=mysecretkey \
                        --build-arg MINIO_INTERNAL_BUCKET=internal-storage-test \
                        --build-arg RABBITMQ_ENDPOINT=goose-01.rmq2.cloudamqp.com \
                        --build-arg RABBITMQ_USER=oaoesvtq \
                        --build-arg RABBITMQ_PASS=nnyfgti9CbBnS4-6Oq6iSWMncUhscG5d \
                        --build-arg RABBITMQ_VHOST=oaoesvtq \
                        --build-arg RABBITMQ_QUEUE_DOWNLOADCOMPLETED=sitas-test-queue-downloadcompleted \
                        --build-arg MINIO_NODE_ENDPOINT=minio-oscarvx00.cloud.okteto.net \
                        --build-arg MINIO_NODE_USER=myaccesskey \
                        --build-arg MINIO_NODE_PASS=mysecretkey \
                        --build-arg MINIO_NODE_BUCKET=node-storage-test \
                        -t oscarvicente/sitas_storagemanager_prod  .
                    """
                    withCredentials([string(credentialsId: 'dockerhub-pass', variable: 'pass')]) {
                        sh "docker login --username oscarvicente --password $pass; docker push oscarvicente/sitas_storagemanager_prod"
                    }

                    //Run local okteto, copy dockerfile
                    script {
                        def output = sh script: "docker run -t -d sitas-okteto-deploy", returnStdout: true
                        def okteto_container = output.trim()
                        sh "echo $okteto_container"
                        sh "docker cp docker-compose.yaml $okteto_container:/sitas/docker-compose.yaml"
                        sh "docker exec $okteto_container okteto deploy --wait"
                        sh "docker stop $okteto_container"
                    }

                }
            }
        }
    }
    post{
        always {
            cleanWs()
            sh 'echo end'
        }
    }
}