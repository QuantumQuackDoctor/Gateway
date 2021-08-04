pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                sh "mvn clean install -X -Dspring.datasource.url=jdbc:postgresql://localhost:5432/test"
            }
        }
    }
}