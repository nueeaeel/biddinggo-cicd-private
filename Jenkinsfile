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
        - name: git
          image: alpine/git:2.45.2
          command:
          - cat
          tty: true
      '''
    }
  }

  environment {
    FRONTEND_JOB = 'biddinggo-frontend'
    BACKEND_JOB = 'biddinggo-backend'
  }

  stages {
    stage('Checkout Repository') {
      steps {
        checkout scm
      }
    }

    stage('Check CI Skip') {
      steps {
        container('git') {
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
    }

    stage('Detect Changed Areas') {
      steps {
        script {
          def changedFiles = sh(script: '''
            if git rev-parse --verify HEAD^ >/dev/null 2>&1; then
              git diff --name-only HEAD^ HEAD
            else
              git show --name-only --pretty=format:'' HEAD
            fi
          ''', returnStdout: true).trim().split('\n').findAll { it }

          env.FRONTEND_CHANGED = changedFiles.any { it.startsWith('frontend/') || it.startsWith('infra/k8s/frontend/') }.toString()
          env.BACKEND_CHANGED = changedFiles.any { it.startsWith('backend/') || it.startsWith('infra/k8s/backend/') }.toString()

          echo "Changed files:\n${changedFiles.join('\n')}"
          echo "Frontend changed: ${env.FRONTEND_CHANGED}"
          echo "Backend changed: ${env.BACKEND_CHANGED}"
        }
      }
    }

    stage('Trigger Jobs') {
      steps {
        script {
          if (env.FRONTEND_CHANGED == 'true') {
            echo "Triggering frontend job: ${env.FRONTEND_JOB}"
            build job: env.FRONTEND_JOB, wait: false
          }

          if (env.BACKEND_CHANGED == 'true') {
            echo "Triggering backend job: ${env.BACKEND_JOB}"
            build job: env.BACKEND_JOB, wait: false
          }

          if (env.FRONTEND_CHANGED != 'true' && env.BACKEND_CHANGED != 'true') {
            echo 'No frontend/backend source changes detected. Nothing to trigger.'
          }
        }
      }
    }
  }

  post {
    always {
      script {
        if (env.FRONTEND_CHANGED == 'true' || env.BACKEND_CHANGED == 'true') {
          echo 'Coordinator pipeline completed and child jobs were triggered as needed.'
        } else {
          echo 'Coordinator pipeline completed with no relevant frontend/backend changes.'
        }
      }
    }
  }
}
