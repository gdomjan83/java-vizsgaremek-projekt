
# Vizsgaremek - Invoice Keeper

## Leírás

Az Invoice Keeper egy vállalkozások és az általuk kiállított számlák nyilvántartására szolgáló backend alkalmazás,
melynek segítségével adatábázisba menthetjük, tárolhatjuk, illetve különféle kritérimok alapján kiolvashatjuk
az entitásként működő számlákat, vállalkozásokat. Továbbá lehetőség van fizetési utasítást indítani a számlákhoz. 

Mivel mindennapi munkám során költségek adminisztrációjával is foglalkozom, úgy gondoltam, hogy az alkalmazás 
kiindulópontot jelenthet akár egy olyan jövőbeli projekthez is, amelynek eredménye segítséget nyújthat jelenlegi munkámban.

---

## Felépítés

### Entitás 1

Az `Invoice` (számla) entitás a következő attribútumokkal rendelkezik:
	
* id - ez a sorszám a számlák egyedi azonosítására szolgál az adatbázisban. IDENTITY generálás révén kerül kiosztásra,
* invoiceNumber - a számlák sorszáma, melyek UNIQUE mezők, tehát szintén egyedileg azonosítják a számlákat,
* issueDate - a számlák kiállításának dátuma,
* dueDate - a számlák fizetési határideje,
* paymentStatus - kifizetés állapotát jelző enum, melynek két értéke lehet: PAYED és UNPAYED,
* items - a számlán lévő tételeket tartalmazó lista, mely egy `InvoiceItem` objektumokat tartalmazó ElementCollection.
* amount - a számla összege,
* company - a kibocsátó vállalkozást (entitást) tartalmazó attribútum.

Az `InvoiceItem` osztály Element Collectionként kapcsolódik a számlákhoz. Attribútumai:
* name - a tétel megnevezése,
* pieces - a tétel darabszáma,
* priceTotal - a tétel ára.


Végpontok:

| HTTP metódus | Végpont                 	| Leírás                                                                 	 |
| ------------ | ---------------------------| ---------------------------------------------------------------------------|
| POST         | `"/api/invoices"`      	| elment egy új számlát az adatbázisba. 								     |
| GET          | `"/api/invoices/{id}"`  	| lekérdezi az adott`id` attribútummal rendelkező számlát.                 	 |
| GET          | `"/api/invoices"`		 	| lekérdezi az összes számlát az URL-hez fűzott keresési feltételek alapján. |
| GET          | `"/api/invoices/find-item"`| megadott tételeket tartalmazó számlák listázása.							 |	
| PUT          | `"/api/invoices/payment"`  | kifizeti a JSON törzsben megadott sorszámú számlát      	          		 |
| DELETE       | `"/api/invoices/{id}"`  	| törli az adatábázisból a megadott `id` azonosítójú számlát.  	      		 |


A utasítások JSON törzsében validálásra kerülnek az alábbiak:
* `vatNumber` (adószámok) formátuma,
* `bankAccountNumber` (bankszámlaszámok) formátuma,
* az entitások nem üres mezői,
* az `issueDate`(kiállítás kelte) nem lehet jövőbeli dátum,
* az `amount`(összeg) minimum összege 1.

Új számla rögzítésekor ellenőrzésre kerül, hogy létezik-e már az adatbázisban számla az adott sorszámmal. Amenyiben nem, akkor az adatok mentésre
kerülnek. Egyébként kivételt kapunk.

Új számla rögzítésekor megvizsgálásra kerül, hogy a számlát kiállító vállalkozás szerepel-e már az adatábázisban. Ha nem, mentésre kerül a vállalkozás is.
Amennyiben már létezik a vállalkozás, akkor a számla hozzáadásra kerül ehhez a vállalkozáshoz.

Fizetési utasítás indításakor megvizsgálásra kerül, hogy a megadott bankszámlaszám megegyezik-e a számlát kiállító vállalkozás bankszámlaszámával. Illetve az 
utasítás összege nem lehet nagyobb, mint a kifizetni kívánt számla összege.

Az `"/api/invoices"` végponton lévő GET kéréskor a következő szűrési feltételek választhatók:
* `companyName` - a vállalkozás neve tartalmazza a megadott szöveget,
* `vatNumber` - a megadott adószámú vállalkozás számláit listázza,
* `issuedAfter` - az adott dátum után kelt számlákat listázza,
* `isOverDue` - a lejárt fizetési határidejű számlákat listázza.

Az `"/api/invoices/find-item"` végponton lévő GET kéréskor átadható az `itemName` paraméter, mely listázza mindazon számlákat, aminek tétel nevei között szerepel
a megadott szövegrészlet.

---

### Entitás 2

A `Company` (vállalkozás) entitás a következő attribútumokkal rendelkezik:
	
* id - ez a sorszám a vállalkozások egyedi azonosítására szolgál az adatbázisban. IDENTITY generálás révén kerül kiosztásra.
* companyName - a vállalkozás nevét tartalmazó String.
* vatNumber - a vállalkozás adószámát tartalmazó String. UNIQUE, azaz egyedileg azonosítja a vállalkozást. Formátuma rögzített (pl. 12345678-1-12), mely validására kerül.
* bankAccountNumber - a vállalkozás adószámát tartalmazó String. Formátuma rögzített (pl. 11111111-22222222-33333333), mely validálásra kerül.
* invoices - a vállalkozás által kibocsátott számlákat (entitás) tartalmazó lista. 

A `Company` és az `Invoice` entitások között kétirányú, 1-n kapcsolat van.

Végpontok:

| HTTP metódus | Végpont                 			  | Leírás                                                                 							  |
| ------------ | -------------------------------------| --------------------------------------------------------------------------------------------------|
| POST         | `"/api/companies"`        			  | lekérdezi az összes vállalkozást.			                             						  |
| POST         | `"/api/companies/{id}"`   			  | a megadott `id` azonosítóval rendelkező vállalkozáshoz elment egy új számlát.   				  |
| GET 	       | `"/api/companies"`   		 	  	  | lekéri az összes vállalkozást az URL-hez fűzött keresési feltétel alapján.			 			  | 
| GET 	       | `"/api/companies/{id}"`   		 	  | lekéri a megadott `id` azonosítóval rendelkező vállalkozást.   					 				  |
| GET 	       | `"/api/companies/vat-number/{vat}"`  | lekéri a megadott `vat` adószámmal (vatNumber) rendelkező vállalkozást.   					   	  |
| PUT 	       | `"/api/companies/{id}"`   		 	  | módosítja a megadott `id` azonosítóval rendelkező vállalkozás bankszámlaszámát.  				  | 
| DELETE 	   | `"/api/companies/{id}"`   		 	  | törli az adatábázisból a megadott `id` azonosítójú vállalkozást és a hozzá kapcsolódó számlákat.  |


A utasítások JSON törzsében validálásra kerülnek az alábbiak:
* `vatNumber` (adószámok) formátuma,
* `bankAccountNumber` (bankszámlaszámok) formátuma,
* az entitások nem üres mezői.

Új vállalkozás rögzítésekor ellenőrzésre kerül, hogy létezik-e már az adatbázisban vállalkozás a megadott adószámmal. Amenyiben nem, akkor az adatok mentésre
kerülnek. Egyébként kivételt kapunk.

Az `"/api/companies"` végponton lévő GET kéréskor átadható a `searchName` paraméter, mely listázza mindazon vállalkozásokat, aminek a neve tartalmazza a megadott
szövegrészletet.

---

## Technológiai részletek

Az alkalmazás 17-es JAVA verzióval készült. A mellékelt JAR fájl elnevezése: invoicekeeper.jar

Az Invoice Keeper háromrétegű alkalmazás, az alábbi rétegekkel:
* Controller - két osztály (CompanyController, InvoiceController), mely a felhasználói felülettel tartják a kapcsolatot. HTTP kéréseket végez.
* Service - egy osztály (InvoicingService), mely az üzleti logikát tartalmazza, kapcsolatot tart a controller és a repository réteg között.
* Repository - két osztály (CompanyRepository, InvoiceRepository), mely az adatbázis műveleteket végzi. Adatokat ment, módosítés gyűjt ki.

Az alkalmazás adatbázis rétege MariaDb adatbázist használ.

A mellékelt Dockerfile segítségével Docker image generálható, így az alkalmazás Dockerből is futtatható.

A dokumentáció SwaggerUI segítségével készült, mely az alkalmazás indítását követően az alábbi URL-en érhető el: http://localhost:8080/swagger-ui.html







---