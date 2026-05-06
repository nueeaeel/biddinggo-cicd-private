pipeline {
  agent any

  environment {
    FRONTEND_JOB = 'biddinggo-frontend'
    BACKEND_JOB = 'biddinggo-backend'
    GHCR_REGISTRY = 'ghcr.io'
    GHCR_OWNER = 'nueeaeel'
    FRONTEND_IMAGE = "${GHCR_REGISTRY}/${GHCR_OWNER}/biddinggo-frontend"
    BACKEND_IMAGE = "${GHCR_REGISTRY}/${GHCR_OWNER}/biddinggo-backend"
    FRONTEND_DEPLOYMENT_MANIFEST = 'infra/k8s/frontend/deployment.yaml'
    BACKEND_DEPLOYMENT_MANIFEST = 'infra/k8s/backend/deployment.yaml'
  }

  stages {
    stage('Check CI Skip') {
      steps {
        script {
          def skipStatus = sh(script: 'git rev-parse --is-inside-work-tree >/dev/null 2>&1 && git log -1 --pretty=%B | grep -q "\\[skip ci\\]"', returnStatus: true)
          env.SKIP_PIPELINE = skipStatus == 0 ? 'true' : 'false'
          if (env.SKIP_PIPELINE == 'true') {
              env.SKIP_REASON = 'latest commit contains [skip ci]'
              echo 'Skipping build because the latest commit contains [skip ci].'
          }
        }
      }
    }

    stage('Detect Changed Areas') {
      when {
        expression { env.SKIP_PIPELINE != 'true' }
      }
      steps {
        script {
          def baseCommit = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT ?: env.GIT_PREVIOUS_COMMIT
          def changedText = ''

          if (baseCommit?.trim() && sh(script: "git cat-file -e ${baseCommit}^{commit}", returnStatus: true) == 0) {
            changedText = sh(script: "git diff --name-only ${baseCommit} HEAD", returnStdout: true).trim()
          } else {
            changedText = sh(script: "git show --name-only --pretty=format:'' HEAD", returnStdout: true).trim()
          }

          def changedFiles = changedText ? changedText.split('\n').findAll { it } : []

          env.FRONTEND_CHANGED = changedFiles.any { it.startsWith('frontend/') || it.startsWith('infra/k8s/frontend/') || it == 'infra/env/frontend.env' }.toString()
          env.BACKEND_CHANGED = changedFiles.any { it.startsWith('backend/') || it.startsWith('infra/k8s/backend/') }.toString()

          echo "Changed files:\n${changedFiles.join('\n')}"
          echo "Frontend changed: ${env.FRONTEND_CHANGED}"
          echo "Backend changed: ${env.BACKEND_CHANGED}"
        }
      }
    }

    stage('Trigger Build Jobs') {
      when {
        expression { env.SKIP_PIPELINE != 'true' }
      }
      steps {
        script {
          if (env.FRONTEND_CHANGED == 'true') {
            echo "Triggering frontend job: ${env.FRONTEND_JOB}"
            def frontendBuild = build job: env.FRONTEND_JOB, wait: true
            env.FRONTEND_BUILD_NUMBER = frontendBuild.number.toString()
          }

          if (env.BACKEND_CHANGED == 'true') {
            echo "Triggering backend job: ${env.BACKEND_JOB}"
            def backendBuild = build job: env.BACKEND_JOB, wait: true
            env.BACKEND_BUILD_NUMBER = backendBuild.number.toString()
          }

          if (env.FRONTEND_CHANGED != 'true' && env.BACKEND_CHANGED != 'true') {
            echo 'No frontend/backend source changes detected. Nothing to trigger.'
          }
        }
      }
    }

    stage('Update Deployment Images') {
      when {
        expression {
          env.SKIP_PIPELINE != 'true' &&
          (env.FRONTEND_CHANGED == 'true' || env.BACKEND_CHANGED == 'true')
        }
      }
      steps {
        script {
          withCredentials([usernamePassword(credentialsId: 'github-token', usernameVariable: 'GITHUB_USER', passwordVariable: 'GITHUB_TOKEN')]) {
            sh 'git config user.name "jenkins-bot"'
            sh 'git config user.email "jenkins-bot@users.noreply.github.com"'
            sh 'git remote set-url origin https://$GITHUB_USER:$GITHUB_TOKEN@github.com/nueeaeel/biddinggo-cicd-private.git'
            sh 'git fetch origin main'
            sh 'git checkout -B main origin/main'

            if (env.FRONTEND_CHANGED == 'true') {
              sh 'sed -i "s|image: $FRONTEND_IMAGE:.*|image: $FRONTEND_IMAGE:$FRONTEND_BUILD_NUMBER|" $FRONTEND_DEPLOYMENT_MANIFEST'
            }

            if (env.BACKEND_CHANGED == 'true') {
              sh 'sed -i "s|image: $BACKEND_IMAGE:.*|image: $BACKEND_IMAGE:$BACKEND_BUILD_NUMBER|" $BACKEND_DEPLOYMENT_MANIFEST'
            }

            sh 'git diff -- infra/k8s/frontend/deployment.yaml infra/k8s/backend/deployment.yaml'
            sh 'git add infra/k8s/frontend/deployment.yaml infra/k8s/backend/deployment.yaml'
            sh 'git diff --cached --quiet && echo "No deployment image changes to commit." || git commit -m "ci: update deployment images [skip ci]"'
            sh 'git pull --rebase origin main'
            sh 'git push origin HEAD:main'
          }
        }
      }
    }
  }

  post {
    success {
      script {
        if (env.SKIP_PIPELINE == 'true') {
          echo """
============================================================
  ⏭️  CI skipped
============================================================

  🧭 Build
    Job        : ${env.JOB_NAME} #${env.BUILD_NUMBER}
    Duration   : ${currentBuild.durationString.replace(' and counting', '')}

  📝 Reason
    Message    : ${env.SKIP_REASON ?: 'latest commit contains [skip ci]'}

============================================================
"""
        } else {
          echo """
============================================================
  ✅ BiddingGo coordinator pipeline succeeded
============================================================

  🧭 Build
    Job        : ${env.JOB_NAME} #${env.BUILD_NUMBER}
    Duration   : ${currentBuild.durationString.replace(' and counting', '')}

  🚦 Trigger
    Frontend   : ${env.FRONTEND_CHANGED == 'true' ? env.FRONTEND_JOB : 'skipped'}
    Backend    : ${env.BACKEND_CHANGED == 'true' ? env.BACKEND_JOB : 'skipped'}

============================================================
"""
        }
      }
    }
    failure {
      echo """
============================================================
  ❌ BiddingGo coordinator pipeline failed
============================================================

  🧭 Build
    Job        : ${env.JOB_NAME} #${env.BUILD_NUMBER}
    Duration   : ${currentBuild.durationString.replace(' and counting', '')}

  🔎 Debug
    Console    : ${env.BUILD_URL}console

============================================================
"""
    }
  }
}
