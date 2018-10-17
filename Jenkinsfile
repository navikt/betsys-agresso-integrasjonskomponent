#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        maven 'default'
    }
    environment {
        LDAP_URL="ldapgw.test.local"
    }
    stages {
        stage('build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('test') {
            steps {
                sh 'mvn test'
                junit 'target/surefire-reports/*.xml'
            }
        }
        stage('deploy docker image') {
            steps {
                script {
                    checkout scm
                    docker.withRegistry('https://docker.adeo.no:5000/') {
                        def image = docker.build("okonomi/betsys-agresso-integrasjonskomponent:1.0.${env.BUILD_ID}")
                        image.push()
                        image.push 'latest'
                    }
                }
            }
        }
        stage('deploy nais.yaml to nexus m2internal') {
            steps {
                script {
                    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'nexus-user', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD']]) {
                        sh "nais validate"
                        sh "nais upload --app betsys-agresso-integrasjonskomponent -v 1.0.${env.BUILD_ID}"
                    }
                }
            }
        }
        stage('deploy to nais') {
            steps {
                script {
                    withCredentials([[$class: "UsernamePasswordMultiBinding", credentialsId: 'nais-user2', usernameVariable: "NAIS_USERNAME", passwordVariable: "NAIS_PASSWORD"]]) {
                        def postBody = [
                                application: "betsys-agresso-integrasjonskomponent",
                                fasitEnvironment: "${fasitEnvironment}",
                                version    : "1.0.${env.BUILD_ID}",
                                fasitUsername   : "${env.NAIS_USERNAME}",
                                fasitPassword   : "${env.NAIS_PASSWORD}",
                                zone       : "fss",
                                namespace  : "default"
                        ]
                        def naisdPayload = groovy.json.JsonOutput.toJson(postBody)

                        echo naisdPayload

                        def response = httpRequest([
                                url                   : "https://daemon.nais.preprod.local/deploy",
                                consoleLogResponseBody: true,
                                contentType           : "APPLICATION_JSON",
                                httpMode              : "POST",
                                requestBody           : naisdPayload,
                                ignoreSslErrors       : true
                        ])

                        echo "$response.status: $response.content"

                        if (response.status != 200) {
                            currentBuild.description = "Failed - $response.content"
                            currentBuild.result = "FAILED"
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            archive 'target/*.jar'
            deleteDir()
        }

    }
}