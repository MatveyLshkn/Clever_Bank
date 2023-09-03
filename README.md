# Clever_Bank
 Stack: 
- Java 17
- Gradle
- PostgreSQL
- JDBC
- Lombok
- Servlets
- OpenPdf

Note: all tables are in 3rd normal form, conversion between currencies as well as unit tests are not provided
  
How to start:
1. open project
2. go to src/main/resources/config.yml
3. set your bankPersentage, set your absoluteProjectPath
4. create your local database using file db_dump\dump.sql
5. start tomcat server
6. you are ready to go!
7. some operations like withdraw, transfer, refill, printAccountStatement work in main method, so you don't have to start tomcat server for this 


What project is able to do:
1. CRUD operations:
   1. For transaction entities:
      - get
        <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/4f8be27f-a161-4413-a537-02eb34228fba)
        <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/c953f156-4e7c-47e7-b965-9aa8b1390de7)
        <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/868e009a-c7fa-4b9e-944b-41b40cc9e908)
      - post
        <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/7c824e49-9106-44df-aac9-9de7433a84ff)
        <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/8d8411bd-daba-4b86-80c2-5d89f86c9ee6)
      - put
        <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/625e466c-60d6-453f-a490-dea431a2c01d)
        <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/c8648625-3ce0-408f-a70e-19d2a4f5912b)
      - delete
        <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/3782d9db-ff12-4a4d-9d30-c7851f4c7afe)
        <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/fc533a66-301c-4639-af8a-73badd0cabbf)
   2. For account entities:
      - get<br>
       <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/ff92e66c-c66d-47c9-a551-bf31c776eed2)
       <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/3b432a87-e5cb-410b-8597-9b5cda501498)
      - post<br>
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/1800711e-0ad5-4cc1-98f5-2da2a1c7527a)
       <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/aa8c6906-ebf9-4967-9750-239435ecf854)
      - put<br>
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/99f10552-0f25-40b3-892f-facb9ab3b69e)
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/3b36e0be-3dd6-4af6-9a28-61884813947d)
      - delete<br>
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/006b2e5d-2d4f-4085-bf3c-eb46ccd433bc)
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/1203f663-fe54-426c-bb20-bbe200995471)
   3. For appUser entities:
      - get<br>
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/ae4f40b7-feb5-4171-ae2c-df33e63e0313)
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/9750f598-d4f4-4cbf-b7e6-5c04a1927ba0)
      - post<br>
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/a4d5a88a-d76b-47d6-94fd-0e5d2368fe39)
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/d27206fd-cd8e-45e6-ac02-35d75068d88f)
      - put<br>
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/d71ffba3-b370-4819-9b71-3c43af0c7982)
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/3eb513f3-ba73-4f0d-a109-6c7f53aafcb2)
      - delete<br>
     <br> ![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/0305abc9-b4cb-461f-9e8c-2314b91503c4)
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/19fe4c44-f60f-4ad3-b4ad-939072b3452b)
   4. For bank entities:
      - get<br>
       <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/2bd63eac-6ae5-4552-9519-cf47c93d12e5)
       <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/beab1a3e-0384-47a2-8c52-c610fb7aa07f)
      - post<br>
     <br> ![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/f6a9addd-2842-4b66-ad24-a55f071489b9)
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/d2a228be-7fda-4506-b501-bd5eb8bd1f6e)
      - put<br>
     <br> ![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/7ba6bc28-1330-405d-bdca-837cad6241cd)
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/b5933aaf-c627-42db-871d-faf44ce542f1)
      - delete<br>
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/2efed1db-6e82-459a-8b88-56f1a89e4b28)
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/978378a1-bf46-4f05-a560-ec21eb22a42f)

2. Money operations(via main class, main method):
   1. Withdraw:
      <br>Account before
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/26f96b4b-7e88-449d-a00f-f86dccc7312f)
      <br>withdraw method
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/ba7b7ac4-9812-4d00-95f9-b142316895e1)
      <br>receipt after operation
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/db69a262-7f8e-4a78-8641-7ee65588ca38)
      <br>Account after
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/702f1e77-8943-4e1d-aefa-9f4582f0956f)
      <br>Created transaction
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/eb4803a4-ad78-4fb6-a23a-d7fa1e9be34d)
   2. Refill:
     <br>Account before
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/3500c0ca-cf28-45f4-b21e-4c6d47049f7e)
     <br>refill method
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/b6754540-2b3d-49d5-b366-d0906b560d18)
     <br>receipt after operation
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/4752da81-4047-45da-9eb4-1083cfa52d77)
     <br>Account after
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/f01b8dcd-ee32-492c-ba10-0100fd46df29)
     <br>Created transaction
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/cab64fc4-613a-4a66-af8b-960a92915b6c)
    3. Transfer:
     <br>In this showcase there was transfer between account with id=1 and account with id=2
     <br>Accounts before
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/779e308c-26f3-4232-8be8-2d6ed8274b88)
     <br>Transfer method
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/2b917b91-db23-43af-a2a0-f8752200953f)
     <br>receipt after operation
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/fa2207cf-392c-4fb4-b756-1f8490c1dda1)
     <br>Accounts after
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/ed28bb16-1f78-444d-81c0-904a0317981c)
     <br>Created transaction
     <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/73857c8b-2d48-4e2c-8ffd-c4f7202a0c94)
3. Creating receipts:
   1. Basic check. It is being created after each money operation. Examples stored in directory "check"
   2. Account statement (prints all transactions of account for one of 3 available periods: CURRENT_MONTH, CURRENT_YEAR, WHOLE_PERIOD):
      <br>called via main method
      <br>printAccount statement method
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/dcd978f0-a286-4abe-b8cb-8940cbb72809)
      <br>example, stored in directory "account-statement"
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/406fccf1-110d-42ee-9c94-bc0aa2ba5980)
   3. Money statement (prints income and outgo of account for any period):
      <br>called via http POST method
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/d090385d-f61e-4025-869a-4cc362d2d218)
      <br>result file extension .pdf, stored in directory statement-money
      <br>![изображение](https://github.com/MatveyLshkn/Clever_Bank/assets/115181274/4c518349-8fdf-4ab5-a207-4bbb204a3be5)
4. Charge percentage (works, when tomcat server is running):
   <br>Regularly, according to a schedule (once every half a minute), program checks whether it is necessary to charge
   percentage (1% - the value is substituted from the configuration file config.yml) on the remainder
   bills at the end of the month      

  


   



    


 

 


         


