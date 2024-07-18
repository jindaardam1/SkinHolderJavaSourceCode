import os
import xml.etree.ElementTree as ET
from datetime import datetime

# Directorios donde se encuentran los archivos XML
xml_folder_items = 'C:/Users/Jagoba/Documents/ProgramaSteamInversionesEjecutable/Items'
xml_folder_registros = 'C:/Users/Jagoba/Documents/ProgramaSteamInversionesEjecutable/Registros'

def get_hashname_from_url(url):
    start_index = url.find('market_hash_name=') + len('market_hash_name=')
    hashname = url[start_index:]
    return hashname

# Listas para almacenar las inserciones SQL
sql_users = []
sql_items = []
sql_user_items = []
sql_registros = []
sql_item_precio = []

# Script SQL inicial
sql_script = "DROP DATABASE IF EXISTS SkinHolderLog;\n\n"
sql_script += "CREATE DATABASE SkinHolderLog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n\n"
sql_script += "USE SkinHolderLog;\n\n"
sql_script += "-- Creación de la tabla Users\n"
sql_script += "CREATE TABLE Users(\n"
sql_script += "    UserID INTEGER PRIMARY KEY AUTO_INCREMENT,\n"
sql_script += "    Username VARCHAR(20) NOT NULL\n"
sql_script += "    CONSTRAINT chk_Username_Length CHECK (CHAR_LENGTH(Username) = 20)"
sql_script += ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n\n"
sql_script += "-- Creación de la tabla LogType\n"
sql_script += "CREATE TABLE LogType(\n"
sql_script += "    LogTypeID INTEGER PRIMARY KEY AUTO_INCREMENT,\n"
sql_script += "    TypeName VARCHAR(7) NOT NULL\n"
sql_script += ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n\n"
sql_script += "-- Creación de la tabla Logger\n"
sql_script += "CREATE TABLE Logger(\n"
sql_script += "    LoggerId INTEGER PRIMARY KEY AUTO_INCREMENT,\n"
sql_script += "    LogDescription VARCHAR(1000) NOT NULL,\n"
sql_script += "    LogDateTime DATETIME NOT NULL,\n"
sql_script += "    LogTypeID INTEGER NOT NULL,\n"
sql_script += "    UserID INTEGER NOT NULL,\n"
sql_script += "    CONSTRAINT fk_LogTypeID_LogType FOREIGN KEY (LogTypeID) REFERENCES LogType(LogTypeID),\n"
sql_script += "    CONSTRAINT fk_UserID_Logger FOREIGN KEY (UserID) REFERENCES Users(UserID)\n"
sql_script += ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n\n"
sql_script += "INSERT INTO LogType (TypeName) VALUES\n"
sql_script += "    ('INFO'),\n"
sql_script += "    ('WARNING'),\n"
sql_script += "    ('ERROR');\n"
sql_script += "\nINSERT INTO Users (Username) VALUES ('JAGOBAZxJo6vKgDdp9nz');\n\n"
sql_script += "DROP DATABASE IF EXISTS SkinHolderDB;\n\n"
sql_script += "CREATE DATABASE SkinHolderDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n"
sql_script += "USE SkinHolderDB;\n\n"
sql_script += "-- Creación de la tabla Users\n"
sql_script += "CREATE TABLE Users(\n"
sql_script += "    UserID INTEGER PRIMARY KEY AUTO_INCREMENT,\n"
sql_script += "    Username VARCHAR(20) NOT NULL\n"
sql_script += "    CONSTRAINT chk_Username_Length CHECK (CHAR_LENGTH(Username) = 20)"
sql_script += ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n\n"
sql_script += "-- Creación de la tabla Items\n"
sql_script += "CREATE TABLE Items(\n"
sql_script += "    ItemID INTEGER PRIMARY KEY AUTO_INCREMENT,\n"
sql_script += "    Nombre VARCHAR(100) NOT NULL,\n"
sql_script += "    HashName VARCHAR(300) NOT NULL\n"
sql_script += ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n\n"
sql_script += "-- Creación de la tabla UserItems\n"
sql_script += "CREATE TABLE UserItems(\n"
sql_script += "    UserItemID INTEGER PRIMARY KEY AUTO_INCREMENT,\n"
sql_script += "    Cantidad INTEGER NOT NULL,\n"
sql_script += "    ItemID INTEGER NOT NULL,\n"
sql_script += "    UserID INTEGER NOT NULL,\n"
sql_script += "    CONSTRAINT fk_ItemID_UserItems FOREIGN KEY (ItemID) REFERENCES Items(ItemID),\n"
sql_script += "    CONSTRAINT fk_UserID_UserItems FOREIGN KEY (UserID) REFERENCES Users(UserID)\n"
sql_script += ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n\n"
sql_script += "-- Creación de la tabla Registros\n"
sql_script += "CREATE TABLE Registros(\n"
sql_script += "    RegistroID INTEGER PRIMARY KEY AUTO_INCREMENT,\n"
sql_script += "    FechaHora DATETIME NOT NULL,\n"
sql_script += "    UserID INTEGER NOT NULL,\n"
sql_script += "    CONSTRAINT fk_UserID_Registros FOREIGN KEY (UserID) REFERENCES Users(UserID)\n"
sql_script += ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n\n"
sql_script += "-- Creación de la tabla ItemPrecio\n"
sql_script += "CREATE TABLE ItemPrecio(\n"
sql_script += "    ItemPrecioID INTEGER PRIMARY KEY AUTO_INCREMENT,\n"
sql_script += "    Precio DECIMAL(10, 2) NOT NULL,\n"
sql_script += "    UserItemID INTEGER NOT NULL,\n"
sql_script += "    RegistroID INTEGER NOT NULL,\n"
sql_script += "    CONSTRAINT fk_UserItemID_ItemPrecio FOREIGN KEY (UserItemID) REFERENCES UserItems(UserItemID),\n"
sql_script += "    CONSTRAINT fk_RegistroID_ItemPrecio FOREIGN KEY (RegistroID) REFERENCES Registros(RegistroID)\n"
sql_script += ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\n\n"
sql_script += "-- Para insertar un usuario tiene que tener mínimo y máximo 20 carácteres."
sql_script += "\nINSERT INTO Users (Username) VALUES ('JAGOBAZxJo6vKgDdp9nz');\n\n"

# ID estático para el usuario
user_id_static = 1

# Diccionario para almacenar ítems y sus IDs
item_id_map = {}
user_item_id_counter = 1
registro_id_counter = 1

# Procesar archivos XML antiguos (Items)
for filename in os.listdir(xml_folder_items):
    if filename.endswith('.xml'):
        xml_file = os.path.join(xml_folder_items, filename)

        tree = ET.parse(xml_file)
        root = tree.getroot()

        nombre = root.find('nombre').text.strip()
        cantidad = int(root.find('cantidad').text.strip())
        url = root.find('id').text.strip()
        hashname = get_hashname_from_url(url)

        if nombre not in item_id_map:
            item_id_map[nombre] = len(item_id_map) + 1
            sql_items.append((item_id_map[nombre], nombre, hashname))

        item_id = item_id_map[nombre]
        sql_user_items.append((user_item_id_counter, cantidad, item_id, user_id_static))
        user_item_id_counter += 1

# Procesar archivos XML nuevos (Registros)
for filename in os.listdir(xml_folder_registros):
    if filename.endswith('.xml'):
        xml_file = os.path.join(xml_folder_registros, filename)

        # Obtener fecha y hora del nombre del archivo
        fecha_hora_str = filename[:-4]  # Eliminar la extensión .xml
        fecha_hora_str = fecha_hora_str.replace('_', ':')
        fecha_hora = datetime.strptime(fecha_hora_str, '%Y-%m-%d %H:%M')
        fecha_hora_formateada = fecha_hora.strftime('%Y-%m-%d %H:%M:%S')

        # Insertar registro de la fecha y hora actual con UserID estático
        sql_registros.append((registro_id_counter, fecha_hora_formateada, user_id_static))

        tree = ET.parse(xml_file)
        root = tree.getroot()

        for item in root.findall('item'):
            nombre = item.find('nombre').text.strip()
            precio = float(item.find('precio').text.strip())

            if nombre not in item_id_map:
                item_id_map[nombre] = len(item_id_map) + 1
                sql_items.append((item_id_map[nombre], nombre, 'No disponible'))
                sql_user_items.append((user_item_id_counter, 0, item_id_map[nombre], user_id_static))
                user_item_id_counter += 1

            item_id = item_id_map[nombre]
            sql_item_precio.append((precio, user_item_id_counter - 1, registro_id_counter))

        registro_id_counter += 1

# Generar SQL para inserciones masivas
sql_script += "INSERT INTO Items (ItemID, Nombre, HashName) VALUES \n"
sql_script += "".join(f"\t({item[0]}, '{item[1]}', '{item[2]}'),\n" for item in sql_items)
sql_script = sql_script[:-2]
sql_script += ";\n\n"

sql_script += "INSERT INTO UserItems (UserItemID, Cantidad, ItemID, UserID) VALUES \n"
sql_script += "".join(f"\t({item[0]}, {item[1]}, {item[2]}, {item[3]}),\n" for item in sql_user_items)
sql_script = sql_script[:-2]
sql_script += ";\n\n"

sql_script += "INSERT INTO Registros (RegistroID, FechaHora, UserID) VALUES \n"
sql_script += "".join(f"\t({item[0]}, '{item[1]}', {item[2]}),\n" for item in sql_registros)
sql_script = sql_script[:-2]
sql_script += ";\n\n"

sql_script += "INSERT INTO ItemPrecio (Precio, UserItemID, RegistroID) VALUES \n"
sql_script += "".join(f"\t({item[0]}, {item[1]}, {item[2]}),\n" for item in sql_item_precio)
sql_script = sql_script[:-2]
sql_script += ";\n\n"

# Mostrar la cantidad de inserciones
print(f"Inserciones de la tabla Users: {len(sql_users)}")
print(f"Inserciones de la tabla Items: {len(sql_items)}")
print(f"Inserciones de la tabla UserItems: {len(sql_user_items)}")
print(f"Inserciones de la tabla Registros: {len(sql_registros)}")
print(f"Inserciones de la tabla ItemPrecio: {len(sql_item_precio)}")

# Guardar el script SQL en un archivo
sql_file = 'SkinHolderDB_migration_script.sql'
with open(sql_file, 'w') as file:
    file.write(sql_script)

print(f"Script SQL generado correctamente en '{sql_file}'")
