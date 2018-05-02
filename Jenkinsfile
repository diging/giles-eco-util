pipeline {
    agent any
    tools { 
        maven 'Maven 3.2.2' 
        jdk 'Java 1.8' 
    }
    stages {
        stage ('Prepare') {
            steps {
                sh 'cd util'
                sh 'mvn test' 
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml' 
                }
            }
        }
    }
}
