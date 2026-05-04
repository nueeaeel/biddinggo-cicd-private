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
              - name: git
                image: alpine/git:2.45.2
                command:
                - cat
                tty: true
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
        GITHUB_REPOSITORY_URL = 'https://github.com/nueeaeel/biddinggo-cicd-private'
    }

    stages {
        stage('Checkout Source') {
            steps {
                container('git') {
                    checkout scm
                    sh 'git status --short'
                }
            }
        }

        stage('Check CI Skip') {
            steps {
                container('git') {
                    script {
                        def skipStatus = sh(script: 'git rev-parse --is-inside-work-tree >/dev/null 2>&1 && git log -1 --pretty=%B | grep -q "\\[skip ci\\]"', returnStatus: true)
                        env.SKIP_PIPELINE = skipStatus == 0 ? 'true' : 'false'
                        if (env.SKIP_PIPELINE == 'true') {
                            echo 'Skipping build because the latest commit contains [skip ci].'
                        }
                    }
                }
            }
        }

        stage('Gradle Build') {
            when {
                expression { env.SKIP_PIPELINE != 'true' }
            }
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
            when {
                expression { env.SKIP_PIPELINE != 'true' }
            }
            steps {
                container('docker') {
                    script {
                        def buildNumber = "${env.BUILD_NUMBER}"
                        withEnv(["DOCKER_IMAGE_VERSION=${buildNumber}"]) {
                            sh 'docker -v'
                            sh 'echo $GHCR_IMAGE_NAME:$DOCKER_IMAGE_VERSION'
                            sh 'docker build --no-cache --label "org.opencontainers.image.source=$GITHUB_REPOSITORY_URL" -t $GHCR_IMAGE_NAME:$DOCKER_IMAGE_VERSION -t $GHCR_IMAGE_NAME:latest .'
                            sh 'docker image inspect $GHCR_IMAGE_NAME:$DOCKER_IMAGE_VERSION'
                            sh 'docker image inspect $GHCR_IMAGE_NAME:$DOCKER_IMAGE_VERSION --format "{{ index .Config.Labels \\"org.opencontainers.image.source\\" }}"'
                        }
                    }
                }
            }
        }

        stage('Push to GHCR') {
            when {
                expression { env.SKIP_PIPELINE != 'true' }
            }
            steps {
                container('docker') {
                    script {
                        def buildNumber = "${env.BUILD_NUMBER}"
                        withEnv(["DOCKER_IMAGE_VERSION=${buildNumber}"]) {
                            withCredentials([usernamePassword(credentialsId: 'github-token', usernameVariable: 'GITHUB_USER', passwordVariable: 'GITHUB_TOKEN')]) {
                                sh 'echo $GITHUB_TOKEN | docker login $GHCR_REGISTRY -u $GITHUB_USER --password-stdin'
                                sh 'docker push $GHCR_IMAGE_NAME:$DOCKER_IMAGE_VERSION'
                                sh 'docker push $GHCR_IMAGE_NAME:latest'
                                sh 'docker logout $GHCR_REGISTRY'
                            }
                        }
                    }
                }
            }
        }

        stage('Update Deployment Image') {
            when {
                expression { env.SKIP_PIPELINE != 'true' }
            }
            steps {
                container('git') {
                    script {
                        def buildNumber = "${env.BUILD_NUMBER}"
                        withEnv(["DOCKER_IMAGE_VERSION=${buildNumber}"]) {
                            withCredentials([usernamePassword(credentialsId: 'github-token', usernameVariable: 'GITHUB_USER', passwordVariable: 'GITHUB_TOKEN')]) {
                                sh 'git config user.name "jenkins-bot"'
                                sh 'git config user.email "jenkins-bot@users.noreply.github.com"'
                                sh 'git checkout main'
                                sh 'git pull --rebase origin main'
                                sh 'sed -i "s|image: $GHCR_IMAGE_NAME:.*|image: $GHCR_IMAGE_NAME:$DOCKER_IMAGE_VERSION|" infra/k8s/deployment.yaml'
                                sh 'git diff -- infra/k8s/deployment.yaml'
                                sh 'git add infra/k8s/deployment.yaml'
                                sh 'git commit -m "ci: update deployment image to $DOCKER_IMAGE_VERSION [skip ci]"'
                                sh 'git remote set-url origin https://$GITHUB_USER:$GITHUB_TOKEN@github.com/nueeaeel/biddinggo-cicd-private.git'
                                sh 'git push origin HEAD:main'
                            }
                        }
                    }
                }
            }
        }
    }
}
