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
        stage ('Deploy') {
            steps {
                dir('deploy') {
                    unstash 'jarFile'
                    sh 'cp -r -a ../sources/containers/prod/. ./'
                    script {
                        docker.build("sitas-storemanager-prod", "--build-arg DATABASE_ENDPOINT=TEST -f Dockerfile .")
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