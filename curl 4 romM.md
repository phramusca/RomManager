# Romm curl commands

## Collections

- Server User and password

  - Get

    ```shell
    curl -u admin:admin http://192.168.1.12/api/collections
    ```

  - Post

    ```shell
    curl -X POST http://192.168.1.12/api/collections -u admin:admin -H 'Content-Type: multipart/form-data' -F 'name=User and pwd'
    ```
  
- Basic Authentication

  - Get

    ```shell
    curl http://192.168.1.12/api/collections -H 'Authorization: Basic YWRtaW46YWRtaW4='
    ```
  
  - Post

    ```shell
    curl -X POST http://192.168.1.12/api/collections -H 'Authorization: Basic YWRtaW46YWRtaW4=' -H 'Content-Type: multipart/form-data' -F 'name=Basic Auth'
    ```

- Bearer Authentication

  - Get a JWT token

    ```shell
    curl -X 'POST' \
      'http://rpi5.local/api/token' \
      -H 'accept: application/json' \
      -H 'Content-Type: application/x-www-form-urlencoded' \
      -d 'grant_type=password&scope=collections.write&username=admin&password=admin'
      ```

  - Post

    ```shell
    curl -X POST http://rpi5.local/api/collections -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6InJvbW06b2F1dGgiLCJzY29wZXMiOiJjb2xsZWN0aW9ucy53cml0ZSIsInR5cGUiOiJhY2Nlc3MiLCJleHAiOjE3MzgzNTI0MjF9.h_B61QB0seik7CMELoDHLAPIjRiWDCePvHrP6CKZ9gM' -H 'Content-Type: multipart/form-data' -F 'name=Using Token'
    ```




