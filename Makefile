all:	build iinstall clean
build:
	ant -f install/build.xml
iinstall:
	cp install/dist/Hpca.war /home/grupo13/apache-tomcat-6.0.26/webapps
	#Uncomment the following line if the database already exists
	#psql -U postgres -h localhost -c "drop database \"HPCA\";"
	psql -U postgres -h localhost -c "create database \"HPCA\";"
	psql -U postgres HPCA < src/db/HPCA.sql
clean:
	rm -r install/build install/dist
uninstall:
	rm /home/grupo13/apache-tomcat-6.0.26/webapps/Hpca.war
	psql -U postgres -h localhost -c "drop database \"HPCA\";"
