# eCommerce

Descripción de la App:

La idea es hacer una pequeña aplicación que consuma el resultado de una llamada a un API y que muestre la lista de comercios y su correspondiente detalle. 


La aplicación incluye las siguientes funcionalidades:
- Los comercios deben poder filtrarse por categoría
- Los comercios deben poder ordenarse por distancia al usuario
- URL a consultar: http://prod.klikin.com/commerces/public

Una vez se obtengan los resultados, cuando "cliquemos" en un registro de la lista:
- Cargará la foto del logotipo de empresa
- Cargará nombre de empresa, descripción, dirección , teléfono y email.
- Habrá un icono, que cuando "cliquemos", nos lanzará el teléfono para poder llamar.
- Habrá un icono, que cuando "cliquemos", nos abrirá nuestro email con la dirección del registro.
- Habrá un icono, que cuando "cliquemos", nos abrirá google maps y nos permitirá mostrar su ubicación (y cómo llegar).
- Habrá un icono, que cuando "cliquemos", nos abrirá la dirección de twitter almacenada en el registro.
- Habrá un icono, que cuando "cliquemos", nos abrirá la dirección de instagram almacenada en el registro.
- Habrá un icono, que cuando "cliquemos", nos abrirá la dirección de facebook almacenada en el registro.


## Especificaciones técnicas


	- Lenguaje de programación Kotlin.
	
	- IDE de Desarrollo:
		- Android Studio 4.0
		- Build #AI-193.6911.18.40.6514223, built on May 20, 2020
        - Runtime version: 1.8.0_242-release-1644-b01 amd64
        - VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
        - OS: Windows 10 10.0
		
	- SDK: minSdkVersion 26 , targetSdkVersion 30
	
	- Librerias empleadas:
		- RETROFIT: 'com.squareup.retrofit2:retrofit:2.4.0'
		- GSON: 'com.google.code.gson:gson:2.8.5'
		- CONVERTER GSON: 'com.squareup.retrofit2:converter-gson:2.4.0'
		- PICASSO: 'com.squareup.picasso:picasso:2.71828'

## Arquitectura y organización

Esta App ha sido realizada en MVC por simplicidad y tiempo.

- Diagrama MVC en el proyecto:
<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/mvc.png">

- Organización o estructura del proyecto:
<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/organizacion.PNG">

## STORYBOARD
En los siguientes screenshots, se puede ver paso a paso el funcionamiento de la app:  
***
@@@@@@@<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_00.png">@@@@@@<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_01.png">





***
&nbsp; &nbsp; <img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_02.png"> &nbsp; &nbsp; <img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_03.png">





***
<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_04.png">"    "<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_05.png">






***
<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_06.png">"     "<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_07.png">





***
<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_08.png">"      "<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_09.png">





***
<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_10.png">"       "<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_11.png">





***
<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_12.png">"         "<img src="https://github.com/antoniomy82/ECommerce_RetrofitKotlin/blob/master/Screenshots/Screenshot_13.png">

