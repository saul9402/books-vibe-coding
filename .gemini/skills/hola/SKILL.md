---
name: hola
description: Skill de prueba (sin tools)
input_schema:
  type: object
  properties:
    nombre:
      type: string
      description: Tu nombre
  required:
    - nombre
---

Di hola de forma amistosa a {{nombre}} en una sola oración.