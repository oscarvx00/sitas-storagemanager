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
                                stash includes: '*.jar', name: 'jarFile'
                                archiveArtifacts artifacts: '*.jar'
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
                AZURE_SERVICE_BUS_CONNECTION_STRING = credentials("AZURE_SERVICE_BUS_CONNECTION_STRING_STORAGEMANAGER_TEST")
                QUEUE_DOWNLOAD_COMPLETED = "download-completed-prod"
                MINIO_NODE_ENDPOINT = "http://oscarvx00.ddns.net:10000"
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
        /*stage ('E2E Test'){
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
                MINIO_NODE_ENDPOINT = "http://oscarvx00.ddns.net:10000"
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
                        --build-arg MONGODB_ENDPOINT="${MONGODB_ENDPOINT}" \
                        --build-arg MONGODB_DATABASE=${MONGODB_DATABASE} \
                        --build-arg MINIO_INTERNAL_ENDPOINT="${MINIO_INTERNAL_ENDPOINT}" \
                        --build-arg MINIO_INTERNAL_USER=${MINIO_INTERNAL_USER} \
                        --build-arg MINIO_INTERNAL_PASS=${MINIO_INTERNAL_USER} \
                        --build-arg MINIO_INTERNAL_BUCKET=${MINIO_INTERNAL_BUCKET} \
                        --build-arg RABBITMQ_ENDPOINT="${RABBITMQ_ENDPOINT}" \
                        --build-arg RABBITMQ_USER=${RABBITMQ_USER} \
                        --build-arg RABBITMQ_PASS=${RABBITMQ_PASS} \
                        --build-arg RABBITMQ_VHOST=${RABBITMQ_VHOST} \
                        --build-arg RABBITMQ_QUEUE_DOWNLOADCOMPLETED=${RABBITMQ_QUEUE_DOWNLOADCOMPLETED} \
                        --build-arg MINIO_NODE_ENDPOINT="${MINIO_NODE_ENDPOINT}" \
                        --build-arg MINIO_NODE_USER=${MINIO_INTERNAL_USER} \
                        --build-arg MINIO_NODE_PASS=${MINIO_INTERNAL_USER} \
                        --build-arg MINIO_NODE_BUCKET=${MINIO_NODE_BUCKET} \
                        -t sitas-storagemanager-e2e  .
                    """
                    //Run container
                    sh script: "docker run sitas-storagemanager-e2e"
                }
            }
        }*/
        stage ('Deploy') {
            environment {
                JAVA_HOME='/usr/lib/jvm/default-jvm'
            }
            steps {
                dir('deploy') {
                    //sh 'cp -r -a ../sources/. ./'
                    sh 'cp -r -a containers/prod/. ./'
                    unstash 'jarFile'

                    sh """
                    docker build -t oscarvicente/sitas-storagemanager-prod  .
                    """
                    withCredentials([string(credentialsId: 'dockerhub-pass', variable: 'pass')]) {
                        sh "docker login --username oscarvicente --password $pass; docker push oscarvicente/sitas-storagemanager-prod"
                    }

                    //Deploy in k8s, server configured
                    dir('kube'){
                        sh 'kubectl delete deploy -n sitas sitas-storagemanager'
                        sh 'kubectl apply -f sitas-storagemanager-deploy.yaml'
                    }

                }
            }
        }
    }
}