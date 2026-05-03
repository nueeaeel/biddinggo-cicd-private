pipeline {
    agent {
        kubernetes {
            yaml '''
            apiVersion: v1
            kind: Pod
            metadata:
              name: jenkins-agent
            spec:
              containers:
              - name: gradle
                image: gradle:8.4-jdk21-alpine
                command:
                - cat
                tty: true
              - name: docker
                image: docker:29.4.1-cli-alpine3.23
                command:
                - cat
                tty: true
                volumeMounts:
                - mountPath: "/var/run/docker.sock"
                  name: docker-socket
              volumes:
              - name: docker-socket
                hostPath:
                  path: "/var/run/docker.sock"
            '''
        }
    }

    environment {
        DOCKER_IMAGE_NAME = 'biddinggo-service'
        GHCR_REGISTRY = 'ghcr.io'
        GHCR_OWNER = 'nueeaeel'
        GHCR_IMAGE_NAME = "${GHCR_REGISTRY}/${GHCR_OWNER}/${DOCKER_IMAGE_NAME}"
    }

    stages {
        stage('Gradle Build') {
            steps {
                container('gradle') {
                    sh 'pwd'
                    sh 'ls -al'
                    sh 'chmod +x ./gradlew'
                    sh './gradlew -v'
                    sh './gradlew clean build -x test'
                    sh 'ls -al'
                    sh 'ls -al ./build/libs'
                }
            }
        }

        stage('Docker Build') {
            steps {
                container('docker') {
                    script {
                        def buildNumber = "${env.BUILD_NUMBER}"
                        withEnv(["DOCKER_IMAGE_VERSION=${buildNumber}"]) {
                            sh 'docker -v'
                            sh 'echo $DOCKER_IMAGE_NAME:$DOCKER_IMAGE_VERSION'
                            sh 'docker build --no-cache -t $DOCKER_IMAGE_NAME:$DOCKER_IMAGE_VERSION .'
                            sh 'docker image inspect $DOCKER_IMAGE_NAME:$DOCKER_IMAGE_VERSION'
                        }
                    }
                }
            }
        }

        stage('Push to GHCR') {
            steps {
                container('docker') {
                    script {
                        def buildNumber = "${env.BUILD_NUMBER}"
                        withEnv(["DOCKER_IMAGE_VERSION=${buildNumber}"]) {
                            withCredentials([usernamePassword(credentialsId: 'github-token', usernameVariable: 'GITHUB_USER', passwordVariable: 'GITHUB_TOKEN')]) {
                                sh 'echo $GITHUB_TOKEN | docker login $GHCR_REGISTRY -u $GITHUB_USER --password-stdin'
                                sh 'docker tag $DOCKER_IMAGE_NAME:$DOCKER_IMAGE_VERSION $GHCR_IMAGE_NAME:$DOCKER_IMAGE_VERSION'
                                sh 'docker tag $DOCKER_IMAGE_NAME:$DOCKER_IMAGE_VERSION $GHCR_IMAGE_NAME:latest'
                                sh 'docker push $GHCR_IMAGE_NAME:$DOCKER_IMAGE_VERSION'
                                sh 'docker push $GHCR_IMAGE_NAME:latest'
                                sh 'docker logout $GHCR_REGISTRY'
                            }
                        }
                    }
                }
            }
        }
    }
}