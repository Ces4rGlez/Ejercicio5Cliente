pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'
    }

    options {
        timestamps()
    }

    stages {
        stage('Compilar y probar frontend') {
            steps {
                sh 'mvn -B clean verify'
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml',
                  allowEmptyResults: true

            archiveArtifacts artifacts: 'target/*.jar',
                             allowEmptyArchive: true,
                             fingerprint: true
        }
    }
}
