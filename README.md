Please do NOT create a project of any kind in the repo.

Create an "Android Application" project locally, in your own workspace (wherever that may be), 
then softlink/create links in your IDE from the repo into your own workspace for the following
files and folders:

	./src/
	./res/ 
	./libs/
	./AndroidManifest.xml

Then, copy the project.properties file from the repo to the appropriate location in your
workspace.

Please add ./libs/gag.jar to your project's classpath in order to use extended annotations.

Eclipse-specific configuration files can be found in ./eclipse-specific, these files may need
to be updated to fit your personal configuration.