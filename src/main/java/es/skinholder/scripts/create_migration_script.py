import os
import xml.etree.ElementTree as ET
from datetime import datetime

# Directorios donde se encuentran los archivos XML
xml_folder_items = 'C:/Users/Jagoba/Documents/SkinHolderJavaPersonal/Items'
xml_folder_registros = 'C:/Users/Jagoba/Documents/SkinHolderJavaPersonal/Registros'

# Ruta al archivo SQL de creación de la base de datos
sql_creation_file = 'C:/Users/Jagoba/Documents/SkinHolderJavaPersonal/create_db_mysql.sql'
gamerpay_itemnames_update_file = 'C:/Users/Jagoba/Documents/SkinHolderJavaPersonal/gamerpay_itemnames_update.sql'

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

# Leer el contenido del archivo SQL de creación de la base de datos
with open(sql_creation_file, 'r', encoding='utf-8') as file:
    sql_creation_script = file.read()

# Generar SQL para inserciones masivas
sql_script = sql_creation_script + "\n\n"
sql_script += "INSERT INTO Items (ItemID, Nombre, HashNameSteam, GamerPayNombre) VALUES \n"
sql_script += "".join(f"\t({item[0]}, '{item[1]}', '{item[2]}', ''),\n" for item in sql_items)
sql_script = sql_script[:-2]
sql_script += ";\n\n"

sql_script += "INSERT INTO UserItems (UserItemID, Cantidad, PrecioMedioCompra, ItemID, UserID) VALUES \n"
sql_script += "".join(f"\t({item[0]}, {item[1]}, 0, {item[2]}, {item[3]}),\n" for item in sql_user_items)
sql_script = sql_script[:-2]
sql_script += ";\n\n"

sql_script += "INSERT INTO Registros (RegistroID, FechaHora, UserID, RegistroTypeID) VALUES \n"
sql_script += "".join(f"\t({item[0]}, '{item[1]}', {item[2]}, 1),\n" for item in sql_registros)
sql_script = sql_script[:-2]
sql_script += ";\n\n"

sql_script += "INSERT INTO ItemPrecio (PrecioSteam, PrecioGamerPay, UserItemID, RegistroID) VALUES \n"
sql_script += "".join(f"\t({item[0]}, 0, {item[1]}, {item[2]}),\n" for item in sql_item_precio)
sql_script = sql_script[:-2]
sql_script += ";\n\n"

# Leer el contenido del del archivo que contiene los nombres de los items en GamerPay
with open(gamerpay_itemnames_update_file, 'r', encoding='utf-8') as file:
    gamerpay_itemnames_update_script = file.read()

sql_script += gamerpay_itemnames_update_script

# Mostrar la cantidad de inserciones
print(f"Inserciones de la tabla Users: {len(sql_users)}")
print(f"Inserciones de la tabla Items: {len(sql_items)}")
print(f"Inserciones de la tabla UserItems: {len(sql_user_items)}")
print(f"Inserciones de la tabla Registros: {len(sql_registros)}")
print(f"Inserciones de la tabla ItemPrecio: {len(sql_item_precio)}")

# Guardar el script SQL en un archivo
sql_file = 'SkinHolderDB_inserts.sql'
with open(sql_file, 'w') as file:
    file.write(sql_script)

print(f"Script SQL generado correctamente en '{sql_file}'")
