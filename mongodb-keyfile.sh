#!/bin/bash

# Cria um arquivo de chave seguro para autenticação do MongoDB replicaset
echo "Criando arquivo de chave para MongoDB..."
openssl rand -base64 756 > mongo-keyfile
chmod 400 mongo-keyfile

# Garantir que o Docker possa ler o arquivo
echo "Ajustando permissões..."
sudo chown 999:999 mongo-keyfile

echo "Arquivo de chave criado com sucesso: mongo-keyfile"
echo "Agora você pode executar o docker-compose."