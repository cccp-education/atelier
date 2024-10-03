# -*- coding: utf-8 -*-
import json
from typing import Dict

import pandas as pd  # pip install pandas
import xmlschema  # pip install xmlschema
import yaml
from pydantic import BaseModel
from pyrsistent import m, PMap


class Signup(BaseModel):
    login: str
    password: str
    repassword: str
    email: str

    @classmethod
    def from_persistent(cls, data: PMap) -> 'Signup':
        if isinstance(data, PMap):
            # Convertit explicitement chaque clé-valeur pour les données Pyrsistent
            signup_dict = {
                'login': str(data['login']),
                'password': str(data['password']),
                'repassword': str(data['repassword']),
                'email': str(data['email'])
            }
            return cls(**signup_dict)
        raise ValueError("Les données d'entrée doivent être de type PMap")

    def to_persistent(self) -> PMap:
        # Utilise model_dump() pour Pydantic v2
        return m(**self.model_dump())

    def to_json(self) -> str:
        """Convert a Pyrsistent Map to a JSON string."""
        return json.dumps(dict(**self.model_dump()))

    def to_xml(self, root_element: str = "signup") -> str:
        """Convert a Pyrsistent Map to an XML string."""
        xml_elements = []
        data = dict(**self.model_dump())
        for key, value in data.items():
            xml_elements.append(f"<{key}>{value}</{key}>")
        return f"<{root_element}>{''.join(xml_elements)}</{root_element}>"

    def to_schema(self, schema_type: str = "json") -> Dict:
        """Generate a JSON or YAML schema from a Pyrsistent Map."""
        schema = {
            "type": "object",
            "properties": {}
        }
        data = dict(**self.model_dump())
        for key, value in data.items():
            schema["properties"][key] = {"type": type(value).__name__}
        if schema_type == "yaml":
            return yaml.dump(schema)
        return schema

    def to_dtd(self, root_element: str = "signup") -> str:
        """Generate a DTD (Document Type Definition) string from the model."""
        dtd_elements = []
        data = dict(**self.model_dump())
        for key, value in data.items():
            dtd_elements.append(f"<!ELEMENT {key} (#PCDATA)>")
        return f"<!ELEMENT {root_element} ({' , '.join(dtd_elements)})>"

    def to_xsd(self, root_element: str = "signup") -> str:
        """Generate an XML Schema (XSD) string from the model.

        Args:
            root_element (str): The name of the root element in the schema

        Returns:
            str: The generated XSD schema as a string
        """
        # Convertir le modèle en DataFrame pandas pour faciliter la manipulation
        data = dict(**self.model_dump())
        df = pd.DataFrame([data])

        # Mapper les types Python vers les types XSD
        xsd_type_mapping = {
            'str': 'xs:string',
            'int': 'xs:integer',
            'float': 'xs:decimal',
            'bool': 'xs:boolean',
            'datetime': 'xs:dateTime'
        }

        # Générer les éléments du schéma
        elements = []
        for column in df.columns:
            python_type = type(data[column]).__name__
            xsd_type = xsd_type_mapping.get(python_type, 'xs:string')
            element = f"""
                <xs:element name="{column}" type="{xsd_type}">
                    <xs:annotation>
                        <xs:documentation>Field: {column}</xs:documentation>
                    </xs:annotation>
                </xs:element>"""
            elements.append(element)

        # Construire le schéma XSD complet
        xsd_schema = f"""<?xml version="1.0" encoding="UTF-8"?>
    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
        <xs:element name="{root_element}">
            <xs:complexType>
                <xs:sequence>
                    {''.join(elements)}
                </xs:sequence>
            </xs:complexType>
        </xs:element>
    </xs:schema>"""

        # Valider le schéma généré avec xmlschema
        try:
            xmlschema.XMLSchema(xsd_schema)
        except Exception as e:
            raise ValueError(f"Le schéma XSD généré n'est pas valide : {str(e)}")

        return xsd_schema

# if __name__ == "__main__":
#     # Création d'un objet avec des données Pyrsistent
#     signup_data = m(
#         login="johndoe",
#         password="secret",
#         repassword="secret",
#         email="johndoe@example.com"
#     )
#
#     # Création d'une instance de Signup à partir des données Pyrsistent
#     signup = Signup.from_persistent(signup_data)
#
#     print("signup:", signup)
#
#     print(f"to_json : {signup.to_json()}")
#
#     print(f"to_xml : {signup.to_xml()}")
#
#     print(f"to_schema json : {signup.to_schema()}")
#
#     print(f"to_schema yaml : {signup.to_schema("yaml")}")
#
#     print(f"to_dtd : {signup.to_dtd()}")
#
#     print(f"to_xsd : {signup.to_xsd()}")
