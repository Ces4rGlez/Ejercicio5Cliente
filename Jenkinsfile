pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'
    }

    stages {
        stage('Verificar JDK 21 y Maven') {
            steps {
                sh '''
                    java -version
                    java -version 2>&1 | grep -q 'version "21' || {
                        echo "Jenkins debe ejecutar con JDK 21" >&2
                        exit 1
                    }
                    mvn -version
                '''
            }
        }

        stage('Compilar y probar frontend') {
            steps {
                sh 'mvn -B clean verify'
            }
        }

        stage('Analisis SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn -B sonar:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        always {
            recordCoverage tools: [[
                parser: 'JACOCO',
                pattern: 'target/site/jacoco/jacoco.xml'
            ]]

            archiveArtifacts artifacts: 'target/*.jar',
                             allowEmptyArchive: true,
                             fingerprint: true
        }
    }
}
