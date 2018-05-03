pipeline {
    agent any
    tools {
        maven 'Maven 3.2.2'
        jdk 'Java 1.8'
    }
    stages {
        /*stage ('Prepare Release') {
            steps {
                sh '''
                    cd util
                    pwd
                    mvn -B jgitflow:release-start -DscmCommentPrefix="[Jenkins] "
                '''
            }
        }*/
        stage ('Tag Release') {
            steps {
                sh '''
                    cd util
                    mvn -B jgitflow:release-finish -DnoDeploy=true -DscmCommentPrefix="[Jenkins] "
                '''
            }
        }
        stage ('Deploy Release') {
            steps {
                sh '''
                    cd util
                    git checkout master
                    mvn -B clean deploy
                '''
            }
        }
    }
}
