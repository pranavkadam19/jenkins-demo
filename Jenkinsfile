pipeline {
    agent any

    environment {
        DOCKERHUB_USER = 'pranav1119'
        IMAGE_NAME = 'spring-boot-app'
        CONTAINER_NAME = 'spring-boot-app'
        APP_PORT = '8081'
    }

    options {
        skipDefaultCheckout(true)
        timestamps()
        disableConcurrentBuilds()

        buildDiscarder(
            logRotator(
                numToKeepStr: '20',
                artifactNumToKeepStr: '10'
            )
        )
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvnw.cmd clean verify -Dspring.profiles.active=dev'
            }
        }

        stage('Prepare Image Tag') {
            steps {
                script {
                    env.IMAGE_TAG = "${BUILD_NUMBER}"
                    env.FULL_IMAGE =
                        "${DOCKERHUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
                }

                echo "Building image: ${FULL_IMAGE}"
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t "%FULL_IMAGE%" .'
            }
        }

        stage('Docker Hub Login') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN'
                    )
                ]) {
                    bat '''
                        echo %DOCKER_TOKEN% | docker login -u %DOCKER_USER% --password-stdin
                    '''
                }
            }
        }

        stage('Push Image') {
            steps {
                bat 'docker push "%FULL_IMAGE%"'
            }
        }

        stage('Deploy') {
            steps {
                bat '''
                    docker pull "%FULL_IMAGE%"

                    docker rm -f "%CONTAINER_NAME%" 2>NUL

                    docker run -d ^
                        --name "%CONTAINER_NAME%" ^
                        --restart unless-stopped ^
                        -p "%APP_PORT%:8080" ^
                        -e SPRING_PROFILES_ACTIVE=docker ^
                        "%FULL_IMAGE%"
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                bat '''
                    timeout /t 10 /nobreak

                    docker ps --filter "name=%CONTAINER_NAME%"

                    docker logs --tail 50 "%CONTAINER_NAME%"
                '''
            }
        }
    }

    post {
        success {
            echo "Deployment successful: ${FULL_IMAGE}"
        }

        failure {
            echo 'Pipeline failed. Check Jenkins console output.'
        }

        always {
            bat 'docker image prune -f'
        }
    }
}