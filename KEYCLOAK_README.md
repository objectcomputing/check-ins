Keycloak is an out-of-the-box authorization tool that can use Oauth and OpenId to provide user authorization to web application.

When the docker-compose up command is run in the project directory, there is a file caclled realm-export.json that will enable the Keycloak console to run and communicate with the application.  The realm and client will be spun up and configured, but there are a coupleof thing s to do before running the application. First, the client-esecret must be copied from Keycloak and pasted into the application-local.yml file.  Then Users must be added to Keycloak so they can be accessed via the application.

To Use Keycloak to provide authorization for the Check-ins project on localhost:
1. Run docker-compose up in terminal(if you haven't already run it)
2. Open Keycloak admin terminal at localhost:8180
3. Click Administration Console to open the Keycloak console
4. Enter the Admin username and password
5. At the top left of the console, make sure you are in the Check-ins-spike realm
6. On the Configure menu on the left of the console, click Clients, then click check-ins-spike-client
  a. Click on Credentials tab and copy the Secret displayed on this tab
  b. Open application-local.yml file in code editor and paste secret into the oauth.clients.keycloak.client-secret field
7. In the Keycloak Admin console, go to the Manage menu and click Users and Add User
  a. Fill in the fields for the user, using the email that is in the work email field in the Check-ins database, 
  b. Make sure User Enabled switch is on and click Save
8. Click on the Credentials tab for the new user 
  a. Type in a password
  b. Type in password password confirmation
  c. Set the Temporary field to OFF
  d. Click Save
9. Click on the User's Role Mappings tab 
  a. Click on the user's role(s) 
  b. click Add Selected
10. Repeat for any other users you need to add, to view users with other roles in the application
11. Run the application as usual, and when the login screen comes up, put in the keycloak username and password for the user with the role you wish to view

