pipeline{
    agent any
    stages{
        stage('Preparando los servicios del proyecto sgu'){
            steps{
                bat '''
                    docker compose -p sgu-ayvc-10b down || exit /b 0
                '''
            }
        }

        stage('Eliminando imagenes anteriores del proyecto'){
            steps{
                bat '''
                    for /f "tokens=*" %%i in ('docker images --filter "label=com.docker.compose.project=sgu-ayvc-10b" -q') do (
                        docker rmi -f %%i
                    )
                    if errorlevel 1 (
                        echo No hay imagenes por eliminar
                    ) else (
                        echo Imagenes eliminadas correctamente
                    )
                '''
            }
        }

        stage('Obteniendo actualizacion...'){
            steps{
                checkout scm
            }
        }

        stage('Construyecto y desplegando los servicios de docker...'){
            steps{
                bat '''
                    docker compose up --build -d
                '''
            }
        }
    }


    post{
        success{
            echo'Pipeline ejecutada correctamente'
        }

        failure {
            echo 'Hubo un error al ejecutar el pipeline'
        }

        always {
            echo 'Pipeline finalizado'
        }
    }
}