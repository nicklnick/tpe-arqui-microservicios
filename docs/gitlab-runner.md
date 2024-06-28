# Configuración de un GitLab Runner

Vamos a configurar un contenedor de GitLab Runner **con acceso al Docker daemon del host**. Esto es útil si queremos correr comandos de `docker` dentro del Runner.

> <u>Prerrequisitos</u>:
>
> -   Tener una instancia EC2:
>     -   _Sistema operativo_: Ubuntu x64
>     -   _Tipo de instancia_: t3.medium
>     -   _Almacenamiento_ > 30Gb

## 1. Instalar Docker en la instancia EC2

Para que nuestra instancia EC2 pueda funcionar como GitLab Runner, debemos instalar Docker.

1. Utilizaremos `apt-get` para instalar Docker . Por esto, es importante en primer lugar actualizar el índice de paquetes para obtener las últimas versiones de los mismos:

    ```bash
    sudo apt-get update
    ```

2. Ahora realizamos la instalación de Docker con el siguiente comando:

    ```bash
    sudo apt-get install docker.io -y
    ```

3. Luego de instalar Docker, debemos inicializar el _Docker Service_:

    ```bash
    sudo systemctl start docker
    ```

4. Podemos verificar que la instalación de Docker haya sido exitosa, mediante el comando:

    ```bash
    sudo docker run --rm hello-world
    ```

5. Si todo ha salido bien, podemos establecer que el servicio de Docker comience automáticamente cada vez que la instancia se active. Para esto ejecutamos:

    ```bash
    sudo systemctl enable docker
    ```

6. Es recomendable añadir al usuario actual del sistema operativo como usuario Docker para poder correr comandos `docker` sin la necesidad del prefijo `sudo`:

    ```bash
    sudo usermod -a -G docker $(whoami)
    ```

7. Reiniciar la instancia EC2.

8. Para verificar que el paso 6 surtió efecto podemos probar repetir el paso 4 sin `sudo`.

## 2. Instalar el GitLab Runner

Para esta parte vamos a basarnos en estas guías:

-   https://docs.gitlab.com/runner/install/docker.html
-   https://docs.gitlab.com/runner/register/index.html#docker

1. Creamos un volumen.

    ```bash
    docker volume create gitlab-runner-config
    ```

2. Empezamos el GitLab Runner montándolo en el volumen que acabamos de crear.

    ```bash
    docker run -d --name gitlab-runner --restart always \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v gitlab-runner-config:/etc/gitlab-runner \
    gitlab/gitlab-runner:latest
    ```

Con el Runner creado, vamos a registrarlo en GitLab.

3. Levantamos un container con un Runner efímero.

    ```bash
    docker run --rm -it \
    -v gitlab-runner-config:/etc/gitlab-runner \
    gitlab/gitlab-runner:latest \
    register
    ```

4. El comando pasado nos va a abrir varios prompts, de los cuales los únicos importantes son:
    - <u>GitLab URL</u>: `https://gitlab.com`
    - <u>Auth Token</u>: `glrt-<token>` (obtenido de la página de GitLab en: `<repo> > Settings > CI/CD > Runners > New project runner > (completamos los datos) > Step 2`)
    - <u>Executor</u>: `docker`
    - <u>Image</u>: `docker:latest`

Con el Runner configurado volveremos al sistema Host. Ahora solo queda hacer algunos cambios en el `config.toml` (archivo de configuración del Runner).

5. Corremos un contenedor efímero para modificar el archivo.

    ```bash
    docker run --rm -it -v gitlab-runner-config:/mnt alpine sh
    ```

6. Ejecutamos `vi /mnt/config.toml` y cambiamos los siguientes campos:
    - `privileged=true`
    - En `volumes` agregar: `["/cache", "/var/run/docker.sock:/var/run/docker.sock"]`
