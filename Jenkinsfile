pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'
    }

    options {
        timestamps()
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

        stage('Escaneo de Vulnerabilidades Semgrep') {
            steps {
                sh '''
                    mkdir -p reportes
                    if command -v docker >/dev/null 2>&1; then
                        docker run --rm -v "$(pwd):/src" returntocorp/semgrep semgrep scan \
                            --config auto \
                            --exclude target \
                            --json -o /src/reportes/semgrep-frontend.json || true
                    elif command -v semgrep >/dev/null 2>&1; then
                        semgrep scan --config auto --exclude target --json -o reportes/semgrep-frontend.json || true
                    fi
                '''
            }
        }

        stage('Analisis SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn -B sonar:sonar \
                            -Dsonar.projectKey=com.mx.cbtis:Ejercicio5Cliente \
                            -Dsonar.projectName="Ejercicio5Cliente" \
                            -Dsonar.java.source=21 \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -Dsonar.exclusions=**/target/**
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            echo "Quality Gate status: ${qg.status} - Continuando sin abortar el pipeline"
                            currentBuild.result = 'UNSTABLE'
                        }
                    }
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

            archiveArtifacts artifacts: 'target/*.jar, reportes/*.json',
                             allowEmptyArchive: true,
                             fingerprint: true
        }
        success {
            echo 'Pipeline frontend completado exitosamente'
        }
        unstable {
            echo 'Pipeline completado con advertencias - Revisar Quality Gate en SonarQube'
        }
        failure {
            echo 'Pipeline fallido - Revisar errores de compilacion o pruebas'
        }
    }
}
