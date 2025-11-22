pipeline {
    agent any

    tools {
        jdk 'java-11'
        maven 'maven'
    }

    stages {

        stage('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/bgoudar25/Docker-Project.git'
            }
        }

        stage('Code Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Code Package') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Docker Build Image') {
            steps {
                sh 'docker build -t bgoudar/docker-project:latest .'
            }
        }

        stage('Run Container') {
            steps {
                sh '''
                    docker rm -f c8 || true
                    docker run -d --name c8 -p 9008:8080 bgoudar/docker-project:latest
                '''
            }
        }

        stage('Docker Login') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                        sh 'echo "$PASS" | docker login -u "$USER" --password-stdin'
                    }
                }
            }
        }

        stage('Push Image to Docker Hub') {
            steps {
                sh '''
                    docker push bgoudar/docker-project:latest
                '''
            }
        }

    }
}
