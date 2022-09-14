pipeline {
    agent {label '!master'}
    stages {
        stage ('Checkout') {
            steps {
                git url: 'https://github.com/oscarvx00/sitas-storagemanager', branch: 'main'
            }
        }
        stage ('Build'){
            steps {
                dir('containers/build'){
                    sh 'cp ../../ ./'
                    buildContainer = docker.build("sitas-storagemanager-buildcontainer").inside('-v $WORKSPACE:sitas-storagemanager-build/build/libs')
                    archiveArtifacts artifacts: '*.jar'
                }
            }
        }
    }
}