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
            steps {
                dir('build-image'){
                    sh 'cp -r -a ../sources/. ./'
                    sh 'cp -r -a containers/build/. ./'
                    script {
                        docker.build("sitas-storagemanager-buildcontainer").inside{
                            sh 'export JAVA_HOME=/usr/lib/jvm/default-jvm; ./gradlew jar' //We have to set JAVA_HOME because it takes the env from agent container
                            dir('build/libs'){
                                stash includes: '*.jar', name: 'jarFile'
                            }
                        }
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