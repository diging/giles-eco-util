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
        stage ('Finish Release') {
            steps {
                sh '''
                    cd util
                    mvn -B jgitflow:release-finish -DscmCommentPrefix="[Jenkins] "
                '''
            }
        }
    }
}
