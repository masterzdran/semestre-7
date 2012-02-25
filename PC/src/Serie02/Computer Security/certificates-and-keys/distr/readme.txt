 -- Infra-estrutura de chave pública para testes --
 --------------------------------------------------
 
 1. Autoridades certificadoras
 
	1.1 CA1
		- Autoridade de certificação raiz: "CA1 - Autoridade Certificadora Raiz - Projecto GNS"
		- Autoridade de certificação intermédia: "CA1 - Autoridade Certificadora Intermedia - Projecto GNS"
		- "End entities": Alice, Carol e Eleanor
		
	1.2 CA2
		- Autoridade de certificação raiz: "CA2 - Autoridade Certificadora Raiz - Projecto GNS"
		- Autoridade de certificação intermédia: "CA2 - Autoridade Certificadora Intermedia - Projecto GNS"
		- "End entities": Bob, David
		
2. Pastas e ficheiros

	- Pasta "certs.end.entities"
		- Ficheiros .cer para todas as "end entities" (Alice, Bob, Carol, David, Eleanor).
		- Cada entidade tem três ficheiros com "key usage diferentes"
		
	- Pasta pfx
		- Ficheiros .pfx (formato PKCS #12) com certificados e chaves privadas das "end entities". 
		- A password é "changeit"
		
	- Pasta "certs.CA.intermediate"
		- Ficheiros .cer com os certificados das autoridades de certificação intermédias
		
	- Pasta "trustanchors"
		- Ficheiros .jks com as "trust anchors" (certificados auto-assinados das autoridades de certificação raiz)
		- A password é changeit
	