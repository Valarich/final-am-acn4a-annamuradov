# Mis hábitos

## Descripción general

**Mis hábitos** es una aplicación móvil desarrollada en Android que permite registrar y administrar hábitos personales.

La aplicación utiliza **Firebase Authentication** para el registro e inicio de sesión de usuarios y **Cloud Firestore** para almacenar los datos de cada cuenta.

Cada usuario puede consultar y administrar únicamente sus propios hábitos.

---

## Funcionalidades principales

La aplicación permite:

- Crear una cuenta con nombre, correo electrónico y contraseña.
- Iniciar sesión con correo electrónico y contraseña.
- Mantener la sesión iniciada al volver a abrir la aplicación.
- Mostrar el nombre del usuario autenticado.
- Cerrar la sesión actual.
- Agregar nuevos hábitos.
- Consultar los hábitos almacenados.
- Marcar un hábito como completado o pendiente.
- Eliminar un hábito individual.
- Eliminar todos los hábitos del usuario.
- Mantener los datos guardados después de cerrar o reiniciar la aplicación.
- Separar los hábitos según el usuario autenticado.

---

## Pantallas implementadas

### Pantalla de inicio de sesión

Permite ingresar a una cuenta existente.

Elementos principales:

- Campo de correo electrónico.
- Campo de contraseña.
- Botón **Iniciar sesión**.
- Botón **Crear cuenta**.
- Validación de campos obligatorios.
- Mensajes de éxito o error.

Al iniciar sesión correctamente, la aplicación dirige al usuario a la pantalla principal.

---

### Pantalla de registro

Permite crear una nueva cuenta.

Elementos principales:

- Campo de nombre.
- Campo de correo electrónico.
- Campo de contraseña.
- Campo para confirmar la contraseña.
- Botón **Registrarse**.
- Botón **Ya tengo una cuenta**.

Validaciones implementadas:

- Todos los campos son obligatorios.
- Las contraseñas deben coincidir.
- La contraseña debe tener al menos seis caracteres.

Después del registro:

1. Firebase Authentication crea la cuenta.
2. Cloud Firestore guarda un documento en la colección `usuarios`.
3. El usuario accede automáticamente a la pantalla principal.

---

### Pantalla principal

Muestra la información y los hábitos del usuario autenticado.

Elementos principales:

- Título **Mis hábitos**.
- Saludo personalizado con el nombre del usuario.
- Imagen representativa.
- Botón **Agregar hábito**.
- Botón **Limpiar lista**.
- Botón **Cerrar sesión**.
- Contador total de hábitos.
- Lista dinámica de hábitos.
- Mensaje cuando la lista está vacía.

La lista se actualiza automáticamente cuando cambia la información almacenada en Firestore.

---

### Pantalla para agregar hábitos

Permite registrar un hábito nuevo.

Elementos principales:

- Título **Agregar hábito**.
- Campo de texto para escribir el hábito.
- Botón **Guardar hábito**.
- Validación para evitar hábitos vacíos.

Al guardar, se crea un nuevo documento en la colección `habitos` de Cloud Firestore.

---

## Administración de hábitos

### Crear un hábito

El usuario escribe el nombre del hábito y presiona **Guardar hábito**.

El documento almacenado contiene:

- Nombre del hábito.
- UID del usuario propietario.
- Estado de finalización.
- Fecha de creación.

### Consultar hábitos

La pantalla principal consulta únicamente los documentos cuyo campo `usuarioId` coincide con el UID del usuario autenticado.

Los hábitos se muestran ordenados según su fecha de creación.

### Cambiar el estado

Al presionar brevemente un hábito, su estado cambia entre:

- Pendiente.
- Completado.

Los hábitos completados aparecen tachados y con menor opacidad.

### Eliminar un hábito

Al mantener presionado un hábito, aparece un cuadro de confirmación.

El usuario puede:

- Cancelar la operación.
- Eliminar definitivamente el hábito.

### Eliminar todos los hábitos

El botón **Limpiar lista** muestra una confirmación antes de eliminar todos los hábitos del usuario.

La eliminación se realiza mediante una operación por lote de Firestore.

---

## Autenticación

La aplicación utiliza Firebase Authentication con el proveedor:

- Correo electrónico y contraseña.

Si no existe una sesión activa, la aplicación abre automáticamente la pantalla de inicio de sesión.

Cuando el usuario cierra sesión, se elimina la sesión local y se vuelve a la pantalla de acceso.

---

## Base de datos

La aplicación utiliza Cloud Firestore.

### Colección `usuarios`

Cada documento utiliza como identificador el UID generado por Firebase Authentication.

Campos principales:

- `nombre`
- `email`
- `creadoEn`

### Colección `habitos`

Cada documento representa un hábito.

Campos principales:

- `nombre`
- `usuarioId`
- `completado`
- `creadoEn`

El campo `usuarioId` permite relacionar cada hábito con su propietario.

---

## Seguridad de los datos

Las reglas de Cloud Firestore verifican que el usuario esté autenticado.

Cada usuario puede acceder únicamente:

- A su propio documento dentro de `usuarios`.
- A los hábitos cuyo campo `usuarioId` coincide con su UID.

De esta forma, los datos de diferentes usuarios permanecen separados.

---

## Tecnologías utilizadas

- Java.
- Android Studio.
- Android SDK.
- Firebase Authentication.
- Cloud Firestore.
- Firebase Android SDK.
- Gradle.
- XML para las interfaces.
- Git y GitHub.

---

## Componentes de Android utilizados

- `ConstraintLayout`
- `LinearLayout`
- `ScrollView`
- `TextView`
- `ImageView`
- `Button`
- `EditText`
- `Toast`
- `AlertDialog`
- `Intent`

---

## Organización principal del proyecto

```text
app/
├── google-services.json
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/example/myapplication/
    │   ├── AddHabitActivity.java
    │   ├── Habit.java
    │   ├── LoginActivity.java
    │   ├── MainActivity.java
    │   └── RegisterActivity.java
    └── res/
        ├── drawable/
        ├── layout/
        ├── mipmap/
        ├── values/
        └── xml/