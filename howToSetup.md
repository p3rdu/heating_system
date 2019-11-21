# steps
 
 * create a password hash with python and copy this to a clipboard for next step:  
 `python -c "import crypt,random,string; print(crypt.crypt(input('clear-text password: '), '\$6\$SALT\$'))"` 
 
 
 * create a human readable file 'config' with contents like:
 ```yaml
  passwd:
      users:
        - name: coreuser
          password_hash: "$6$SALT$3MU..."
          ssh_authorized_keys:
            - ssh-rsa AAAAB...
          groups:
            - wheel
            - sudo
 ```
 	    
 * install coreos-ct with brew  
	`brew install coreos-ct`        

 * execute ct to transpile the human readable 'config' to a machine configuration file 'machineconfig'  
	`ct -in-file config -out-file machineconfig`
	
 * now the ignition machine configuration file looks like this    
	`{"ignition":{"config":{},"security":{"tls":{}},"timeouts":{},"version":"2.2.0"},"networkd":{},"passwd":{"users":[{"name":"coreuser","passwordHash":"$6$SALT$3MUM...","sshAuthorizedKeys":["ssh-rsa AAAAB..."]}]},"storage":{},"systemd":{}}`
	
 * base64 encode the ignition machine configuration ( either to a file or alternatively to stdout  )  
	`base64 -i machineconfig > machineconfig-base64` 
	
 * download the core-os vm file  
	 `curl -LO https://stable.release.core-os.net/amd64-usr/current/coreos_production_vmware_ova.ova`
	 
 * expand the ova-package to a ovf structure in wkd  
    `ovftool coreos_production_vmware_ova.ova .`

 * navigate to the newly created vm folder and open the .vmx file with your favorite editor. In the end of the file add two new key-value pairs:  
 guestinfo.coreos.config.data, which should have the previously created base64 encoded ignition configuration as its value.
 and another configuration value to mark the encoding.
 ```
	guestinfo.coreos.config.data = "eyJpZ25pdG..Dj==
 	guestinfo.coreos.config.data.encoding = "base64"
 ```
 	
 * Now provision the VM with Fusion and remember to connect the networking. From the console one can now login with the password created in step one.
 Check the IP address from CoreOS with `ifconfig | grep inet` an now ssh from your host to CoreOS with the key. IP address is 172.16.159.131 in my example.
	`ssh coreuser@172.16.159.131`
	
 
 * Then create a Dockerfile under your project (I did a Gradle project and built that) with content:
``` 
FROM openjdk:8-jre-slim

EXPOSE 1099
EXPOSE 1100

ENV FOO=localhost

RUN mkdir /app

COPY build/libs/*.jar /app/distributedhello.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom", "-Djava.util.logging.SimpleFormatter.format=%1$tc %2$s %4$s: %5$s%6$s%n", "-jar", "/app/distributedhello.jar"]
``` 
 
 * Then build the docker image 
 	`docker build -t p3rdu/distributedhello .`
 	
 * Create a tar file from the docker image
    `docker save -o distributedhello.tar p3rdu/distributedhello`
	
 * And copy this tar over to coreOS
    `scp distributedhello.tar coreuser@172.16.159.129:~`	  
 	
 * On coreOS load the docker image to local repository 
    `sudo docker load -i distributedhello.tar`
  
 * And then run it there 
    `sudo docker run -it p3rdu/distributedhello`   
    
 * The tasks above for docker build, save, copy and load can be automatised with ansible by creating a `buildandcopy.yml` with content: 
 ```YAML
 - hosts: hosts
  tasks:
  
  - name: build docker container
    run_once: true
    local_action:
      module: docker_image
      name: distributedhello
      build:
        path: /Users/psmaatta/NetBeansProjects/mavenproject1/distributedhello
      source: build
      force_source: yes
    tags:
      - build
  
  - name: save image to tar
    run_once: true
    local_action:
      module: docker_image
      name: distributedhello
      archive_path: distributedhello.tar
      source: local
    tags:
      - save
      
  - name: Copy file to remote server
    copy:
      src: distributedhello.tar
      dest: ~/distributedhello.tar
    tags:
      - copy
  
  - name: Load images to image repositories
    docker_image:
      name: p3rdu/distributedhello
      load_path: distributedhello.tar
      source: load
    become: yes
    tags:
      - load
  ```
  
  * And then executing it `ansible-playbook buildandcopy.yml -i hosts` 