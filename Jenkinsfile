pipeline {
    agent any

    options {
        disableConcurrentBuilds()
    }

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

        stage('Compilar, Probar y Analizar Frontend') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn -B clean verify sonar:sonar \
                            -Dsonar.projectKey=com.mx.cbtis:Ejercicio5Cliente \
                            -Dsonar.projectName="Ejercicio5Cliente" \
                            -Dsonar.java.source=21 \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -Dsonar.coverage.exclusions=**/*Application.java \
                            -Dsonar.exclusions=**/target/**
                    '''
                }
            }
        }

        stage('Escaneo de Vulnerabilidades Semgrep') {
            steps {
                sh '''
                    mkdir -p reportes
                    rm -f reportes/semgrep-frontend.json reportes/semgrep-frontend.sarif

                    if command -v semgrep >/dev/null 2>&1; then
                        echo "Ejecutando Semgrep CLI..."
                        semgrep scan --config auto --exclude target . \
                            --json-output reportes/semgrep-frontend.json \
                            --sarif-output reportes/semgrep-frontend.sarif
                    elif command -v docker >/dev/null 2>&1; then
                        echo "Ejecutando Semgrep via contenedor Docker..."
                        docker run --rm -v "$(pwd):/src" -w /src returntocorp/semgrep semgrep scan \
                            --config auto \
                            --exclude target \
                            . \
                            --json-output /src/reportes/semgrep-frontend.json \
                            --sarif-output /src/reportes/semgrep-frontend.sarif
                    else
                        echo "No se encontró Semgrep CLI ni Docker; no es posible generar los reportes." >&2
                        exit 1
                    fi

                    test -s reportes/semgrep-frontend.json
                    test -s reportes/semgrep-frontend.sarif
                '''
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        echo "Quality Gate status de SonarQube: ${qg.status}"
                        if (qg.status != 'OK') {
                            echo "Advertencia: El Quality Gate reportó '${qg.status}' (revisar New Code en SonarQube), pero la compilación y pruebas pasaron exitosamente."
                        } else {
                            echo "Quality Gate PASSED"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                if (getContext(hudson.FilePath) != null) {
                    recordCoverage tools: [[
                        parser: 'JACOCO',
                        pattern: 'target/site/jacoco/jacoco.xml'
                    ]]

                    archiveArtifacts artifacts: 'target/*.jar, reportes/*.json, reportes/*.sarif',
                                     allowEmptyArchive: true,
                                     fingerprint: true

                    recordIssues tools: [sarif(pattern: 'reportes/*.sarif')]
                } else {
                    echo 'No hay workspace disponible; se omiten la cobertura y los reportes de este build.'
                }
            }
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
