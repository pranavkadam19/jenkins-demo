pipeline {
    agent any

    environment {
        DOCKERHUB_USER = 'pranav'
        IMAGE_NAME = 'spring-boot-app'
        CONTAINER_NAME = 'spring-boot-app'
        APP_PORT = '8080'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(
            numToKeepStr: '20',
            artifactNumToKeepStr: '10'
        ))
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean verify'
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
                sh 'docker build -t "$FULL_IMAGE" .'
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
                    sh '''
                        echo "$DOCKER_TOKEN" |
                        docker login -u "$DOCKER_USER" \
                        --password-stdin
                    '''
                }
            }
        }

        stage('Push Image') {
            steps {
                sh 'docker push "$FULL_IMAGE"'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker pull "$FULL_IMAGE"

                    docker rm -f "$CONTAINER_NAME" || true

                    docker run -d \
                        --name "$CONTAINER_NAME" \
                        --restart unless-stopped \
                        -p "$APP_PORT:8080" \
                        "$FULL_IMAGE"
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    sleep 10
                    docker ps --filter "name=$CONTAINER_NAME"
                    docker logs --tail 50 "$CONTAINER_NAME"
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
            sh 'docker image prune -f || true'
        }
    }
}