const {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
  Header, Footer, AlignmentType, HeadingLevel, BorderStyle, WidthType,
  ShadingType, VerticalAlign, PageNumber, LevelFormat, PageBreak
} = require("docx");
const fs = require("fs");

// ── Color palette ──────────────────────────────────────────────
const BLUE_DARK  = "1F3864";
const BLUE_MID   = "2E75B6";
const BLUE_LIGHT = "D6E4F0";
const GREY_BG    = "F2F2F2";
const WHITE      = "FFFFFF";
const GREEN      = "1E7145";
const GREEN_LIGHT= "E2EFDA";

// ── Helpers ─────────────────────────────────────────────────────
function hr(color = BLUE_MID) {
  return new Paragraph({
    spacing: { before: 40, after: 40 },
    border: { bottom: { style: BorderStyle.SINGLE, size: 8, color, space: 1 } },
    children: []
  });
}

function spacer(lines = 1) {
  return Array.from({ length: lines }, () =>
    new Paragraph({ children: [new TextRun("")], spacing: { before: 60, after: 60 } })
  );
}

function heading1(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_1,
    spacing: { before: 360, after: 160 },
    children: [new TextRun({ text, font: "Arial", size: 32, bold: true, color: BLUE_DARK })]
  });
}

function heading2(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_2,
    spacing: { before: 240, after: 120 },
    children: [new TextRun({ text, font: "Arial", size: 26, bold: true, color: BLUE_MID })]
  });
}

function heading3(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_3,
    spacing: { before: 160, after: 80 },
    children: [new TextRun({ text, font: "Arial", size: 22, bold: true, color: BLUE_DARK })]
  });
}

function body(text, options = {}) {
  return new Paragraph({
    spacing: { before: 80, after: 80 },
    children: [new TextRun({ text, font: "Arial", size: 22, ...options })]
  });
}

function bullet(text, bold = false) {
  return new Paragraph({
    numbering: { reference: "bullets", level: 0 },
    spacing: { before: 60, after: 60 },
    children: [new TextRun({ text, font: "Arial", size: 22, bold })]
  });
}

function infoBox(lines, bgColor = BLUE_LIGHT, textColor = BLUE_DARK) {
  const border = { style: BorderStyle.SINGLE, size: 4, color: BLUE_MID };
  const borders = { top: border, bottom: border, left: border, right: border };
  return new Table({
    width: { size: 9360, type: WidthType.DXA },
    columnWidths: [9360],
    rows: [new TableRow({
      children: [new TableCell({
        borders,
        width: { size: 9360, type: WidthType.DXA },
        shading: { fill: bgColor, type: ShadingType.CLEAR },
        margins: { top: 120, bottom: 120, left: 200, right: 200 },
        children: lines.map(l =>
          new Paragraph({
            spacing: { before: 60, after: 60 },
            children: [new TextRun({ text: l.text, font: "Arial", size: 22,
              bold: l.bold || false, color: textColor })]
          })
        )
      })]
    })]
  });
}

function twoColTable(rows, headerBg = BLUE_MID, header = null) {
  const cellBorder = { style: BorderStyle.SINGLE, size: 1, color: "CCCCCC" };
  const borders = { top: cellBorder, bottom: cellBorder, left: cellBorder, right: cellBorder };

  const tableRows = [];

  if (header) {
    tableRows.push(new TableRow({
      children: header.map((h, i) =>
        new TableCell({
          borders,
          width: { size: i === 0 ? 3500 : 5860, type: WidthType.DXA },
          shading: { fill: headerBg, type: ShadingType.CLEAR },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: [new Paragraph({
            children: [new TextRun({ text: h, font: "Arial", size: 20, bold: true, color: WHITE })]
          })]
        })
      )
    }));
  }

  rows.forEach(([left, right], i) => {
    tableRows.push(new TableRow({
      children: [
        new TableCell({
          borders,
          width: { size: 3500, type: WidthType.DXA },
          shading: { fill: i % 2 === 0 ? GREY_BG : WHITE, type: ShadingType.CLEAR },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: [new Paragraph({
            children: [new TextRun({ text: left, font: "Arial", size: 20, bold: true, color: BLUE_DARK })]
          })]
        }),
        new TableCell({
          borders,
          width: { size: 5860, type: WidthType.DXA },
          shading: { fill: i % 2 === 0 ? GREY_BG : WHITE, type: ShadingType.CLEAR },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: [new Paragraph({
            children: [new TextRun({ text: right, font: "Arial", size: 20, color: "333333" })]
          })]
        })
      ]
    }));
  });

  return new Table({
    width: { size: 9360, type: WidthType.DXA },
    columnWidths: [3500, 5860],
    rows: tableRows
  });
}

function threeColTable(rows, headerBg = BLUE_MID, header = null) {
  const cellBorder = { style: BorderStyle.SINGLE, size: 1, color: "CCCCCC" };
  const borders = { top: cellBorder, bottom: cellBorder, left: cellBorder, right: cellBorder };
  const cols = [2800, 3280, 3280];

  const tableRows = [];

  if (header) {
    tableRows.push(new TableRow({
      children: header.map((h, i) =>
        new TableCell({
          borders,
          width: { size: cols[i], type: WidthType.DXA },
          shading: { fill: headerBg, type: ShadingType.CLEAR },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: [new Paragraph({
            children: [new TextRun({ text: h, font: "Arial", size: 20, bold: true, color: WHITE })]
          })]
        })
      )
    }));
  }

  rows.forEach(([c1, c2, c3], i) => {
    tableRows.push(new TableRow({
      children: [c1, c2, c3].map((val, j) =>
        new TableCell({
          borders,
          width: { size: cols[j], type: WidthType.DXA },
          shading: { fill: i % 2 === 0 ? GREY_BG : WHITE, type: ShadingType.CLEAR },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: [new Paragraph({
            children: [new TextRun({ text: val, font: "Arial", size: 20,
              bold: j === 0, color: j === 0 ? BLUE_DARK : "333333" })]
          })]
        })
      )
    }));
  });

  return new Table({
    width: { size: 9360, type: WidthType.DXA },
    columnWidths: cols,
    rows: tableRows
  });
}

// ── Cover page ──────────────────────────────────────────────────
function coverPage() {
  const coverBorder = { style: BorderStyle.SINGLE, size: 24, color: BLUE_MID };
  return [
    ...spacer(3),
    new Paragraph({
      alignment: AlignmentType.CENTER,
      spacing: { before: 0, after: 120 },
      border: { bottom: { style: BorderStyle.SINGLE, size: 12, color: BLUE_MID, space: 1 } },
      children: [new TextRun({ text: "MitoBooks", font: "Arial", size: 40, bold: true, color: BLUE_MID })]
    }),
    new Paragraph({
      alignment: AlignmentType.CENTER,
      spacing: { before: 120, after: 60 },
      children: [new TextRun({ text: "Registro de Cambio de Sistema", font: "Arial", size: 52, bold: true, color: BLUE_DARK })]
    }),
    new Paragraph({
      alignment: AlignmentType.CENTER,
      spacing: { before: 60, after: 60 },
      children: [new TextRun({ text: "Nuevo campo: Edad del Cliente", font: "Arial", size: 36, color: BLUE_MID })]
    }),
    ...spacer(2),
    new Paragraph({
      alignment: AlignmentType.CENTER,
      spacing: { before: 60, after: 60 },
      children: [new TextRun({ text: "Fecha de implementacion: 12 de marzo de 2026", font: "Arial", size: 22, color: "666666" })]
    }),
    new Paragraph({
      alignment: AlignmentType.CENTER,
      spacing: { before: 40, after: 40 },
      children: [new TextRun({ text: "Version: 1.0   |   Estado: Implementado y verificado", font: "Arial", size: 22, color: "666666" })]
    }),
    ...spacer(2),
    infoBox([
      { text: "Clasificacion del cambio: Mejora funcional menor (no breaking change)", bold: true },
      { text: "Impacto en usuarios: Bajo  |  Riesgo operativo: Bajo" },
      { text: "Requiere migracion de datos: No  |  Requiere downtime: No" },
    ]),
    new Paragraph({ children: [new PageBreak()] })
  ];
}

// ── Section 1: Resumen ejecutivo (producto) ─────────────────────
function section1() {
  return [
    heading1("1. Resumen Ejecutivo"),
    hr(),
    body("Este documento describe la incorporacion de un nuevo campo de informacion en el perfil de los clientes dentro del sistema MitoBooks: la edad."),
    ...spacer(1),

    heading2("1.1 Que cambio y por que"),
    body("Hasta ahora, el sistema registraba los datos basicos de cada cliente: nombre, apellido y fecha de nacimiento. A partir de este cambio, los clientes tambien pueden registrar su edad de forma directa, como un dato adicional independiente."),
    ...spacer(1),
    infoBox([
      { text: "Antes del cambio:", bold: true },
      { text: "El sistema guardaba: nombre, apellido, fecha de nacimiento." },
      { text: "" },
      { text: "Despues del cambio:", bold: true },
      { text: "El sistema guarda: nombre, apellido, fecha de nacimiento Y edad." },
    ]),
    ...spacer(1),

    heading2("1.2 Que pueden hacer ahora los usuarios"),
    bullet("Registrar la edad del cliente al momento de crear o actualizar un perfil."),
    bullet("Consultar la edad en cualquier vista o listado donde aparezca la informacion del cliente."),
    bullet("La edad tambien queda registrada automaticamente en cada venta asociada a ese cliente."),
    ...spacer(1),

    heading2("1.3 Que NO cambia"),
    bullet("La forma de ingresar al sistema es identica."),
    bullet("Todos los datos anteriores (nombre, apellido, fecha de nacimiento) se mantienen sin modificacion."),
    bullet("El historial de ventas existente no se ve afectado."),
    bullet("No se requiere ninguna accion por parte de los usuarios para adoptar el cambio."),
    ...spacer(1),

    heading2("1.4 Impacto en el negocio"),
    twoColTable([
      ["Para el equipo de ventas", "Ahora pueden registrar la edad del cliente directamente. Campo disponible en el formulario de cliente."],
      ["Para el equipo de producto", "Se abre la posibilidad de segmentar reportes y analisis por rango de edad."],
      ["Para el cliente final", "Ninguno visible. El cambio es interno del sistema."],
      ["Para operaciones", "Cambio transparente. No requiere intervencion ni configuracion adicional."],
    ], BLUE_MID, ["Area", "Impacto"]),
    new Paragraph({ children: [new PageBreak()] })
  ];
}

// ── Section 2: Detalle funcional ────────────────────────────────
function section2() {
  return [
    heading1("2. Detalle Funcional del Cambio"),
    hr(),

    heading2("2.1 Descripcion del nuevo campo"),
    twoColTable([
      ["Nombre del campo", "Edad (age)"],
      ["Tipo de dato", "Numero entero"],
      ["Rango permitido", "Entre 1 y 120 anos"],
      ["Obligatorio", "Si, al crear o actualizar un cliente"],
      ["Disponible en ventas", "Si, se incluye automaticamente en el registro de cada venta"],
    ], BLUE_MID, ["Propiedad", "Valor"]),
    ...spacer(1),

    heading2("2.2 Validaciones aplicadas"),
    body("El sistema valida automaticamente que el valor ingresado sea correcto antes de guardarlo:"),
    bullet("No puede estar vacio o nulo."),
    bullet("Debe ser un numero mayor o igual a 1."),
    bullet("Debe ser un numero menor o igual a 120."),
    bullet("Si no se cumple alguna condicion, el sistema rechazara la solicitud con un mensaje de error claro."),
    ...spacer(1),

    heading2("2.3 Comportamiento en registros existentes"),
    infoBox([
      { text: "Atencion:", bold: true },
      { text: "Los clientes registrados antes de este cambio no tendran el campo de edad. Esto es esperado y no genera errores en el sistema. El campo simplemente aparecera como 'sin valor' para esos registros." },
      { text: "" },
      { text: "Accion recomendada: Si se requiere que todos los clientes tengan edad registrada, el equipo puede actualizar los perfiles manualmente desde la plataforma.", bold: false },
    ]),
    new Paragraph({ children: [new PageBreak()] })
  ];
}

// ── Section 3: Tecnico (incidentes) ─────────────────────────────
function section3() {
  return [
    heading1("3. Referencia Tecnica para Gestion de Incidentes"),
    hr(),
    body("Esta seccion esta dirigida al equipo de operaciones e ingenieria para soporte en caso de incidentes relacionados con este cambio.", { color: "666666", italics: true }),
    ...spacer(1),

    heading2("3.1 Alcance tecnico del cambio"),
    body("El cambio afecta exclusivamente la capa de datos y validacion. No se modifico ninguna logica de negocio ni flujo de servicio existente."),
    ...spacer(1),
    threeColTable([
      ["Client.java", "model/", "Se agrego el campo: private Integer age;"],
      ["ClientDTO.java", "dto/", "Se agrego: @NotNull @Min(1) @Max(120) private Integer age;"],
      ["ClientMapper.java", "mapper/", "Sin cambios. MapStruct mapea 'age' automaticamente (nombre identico en entidad y DTO)."],
      ["ClientControllerTest.java", "test/controller/", "Constructores actualizados para incluir el nuevo parametro age."],
      ["ClientServiceImplTest.java", "test/service/impl/", "Constructores actualizados para incluir el nuevo parametro age."],
      ["SaleControllerTest.java", "test/controller/", "Constructores de Client y ClientDTO actualizados."],
      ["SaleServiceImplTest.java", "test/service/impl/", "Constructor de Client actualizado."],
    ], BLUE_MID, ["Archivo", "Capa", "Cambio aplicado"]),
    ...spacer(1),

    heading2("3.2 Stack tecnico involucrado"),
    twoColTable([
      ["Lenguaje", "Java 21"],
      ["Framework", "Spring Boot 3.5.6 con Spring WebFlux (reactivo)"],
      ["Base de datos", "MongoDB (documento: coleccion 'clients')"],
      ["Mapeo DTO<->Entidad", "MapStruct (generacion en tiempo de compilacion)"],
      ["Validacion", "Jakarta Bean Validation (@NotNull, @Min, @Max)"],
      ["Manejo de errores", "GlobalErrorHandler: WebExchangeBindException -> HTTP 400"],
    ], BLUE_MID, ["Componente", "Detalle"]),
    ...spacer(1),

    heading2("3.3 Comportamiento esperado por endpoint"),
    threeColTable([
      ["POST /clients", "201 Created", "El body debe incluir 'age' (entero 1-120). Sin este campo o fuera de rango: 400 Bad Request."],
      ["PUT /clients/{id}", "200 OK", "El body debe incluir 'age'. Mismas validaciones que POST."],
      ["GET /clients", "200 OK", "La respuesta incluye 'age' en cada objeto. Clientes previos al cambio retornan age: null."],
      ["GET /clients/{id}", "200 OK", "Igual que GET /clients pero para un cliente especifico."],
      ["DELETE /clients/{id}", "204 No Content", "Sin cambios respecto al comportamiento anterior."],
    ], BLUE_MID, ["Endpoint", "HTTP Status", "Comportamiento con el nuevo campo"]),
    ...spacer(1),

    heading2("3.4 Ejemplo de payload valido"),
    infoBox([
      { text: "POST /clients  -  Request Body (application/json):", bold: true },
      { text: "" },
      { text: '{' },
      { text: '  "firstName": "Maria",' },
      { text: '  "surname": "Lopez",' },
      { text: '  "birthDateClient": "1990-05-15",' },
      { text: '  "age": 35' },
      { text: '}' },
    ], "F4F6FB"),
    ...spacer(1),
    infoBox([
      { text: "Errores posibles y sus causas:", bold: true },
      { text: "" },
      { text: "HTTP 400 - age es null:          El campo 'age' no fue enviado en el request." },
      { text: "HTTP 400 - age < 1:              Se envio un valor de 0 o negativo." },
      { text: "HTTP 400 - age > 120:            Se envio un valor mayor a 120." },
      { text: "HTTP 400 - firstName muy corto:  Otros campos siguen con sus validaciones previas." },
    ], "FFF4F4"),
    ...spacer(1),

    heading2("3.5 Impacto en MongoDB"),
    body("El campo age se almacena directamente en el documento de la coleccion clients. No requiere indices adicionales ni cambios en la configuracion de MongoDB."),
    twoColTable([
      ["Coleccion afectada", "clients"],
      ["Campo nuevo en documento", "age: <Integer>"],
      ["Documentos existentes", "No tienen el campo 'age'. Retornan null en GET. No generan error."],
      ["Coleccion sales", "Sale embebe un snapshot de Client. Las nuevas ventas incluiran 'age'. Las ventas anteriores no tendran el campo."],
      ["Requiere migracion", "No. MongoDB es schema-less; documentos sin 'age' son validos."],
    ], BLUE_MID, ["Aspecto", "Detalle"]),
    ...spacer(1),

    heading2("3.6 Verificacion post-despliegue"),
    body("Pasos recomendados para confirmar que el cambio funciona correctamente en produccion:"),
    bullet("1. Compilacion: ./mvnw clean compile debe terminar con BUILD SUCCESS."),
    bullet("2. Tests: ./mvnw test debe pasar 81 tests con 0 fallos (resultado verificado en desarrollo)."),
    bullet("3. Crear un cliente nuevo via POST /clients incluyendo el campo age y verificar que retorna HTTP 201."),
    bullet("4. Consultar el cliente creado via GET /clients/{id} y verificar que age aparece en la respuesta."),
    bullet("5. Crear una venta asociada al cliente y verificar que el snapshot del cliente en la venta incluye age."),
    ...spacer(1),

    heading2("3.7 Rollback"),
    infoBox([
      { text: "Plan de rollback:", bold: true },
      { text: "" },
      { text: "1. Revertir el commit en Git que introdujo el campo (hash: verificar en git log)." },
      { text: "2. Redesplegar la version anterior del JAR." },
      { text: "3. Los documentos en MongoDB con 'age' guardado no causan error al volver a la version anterior; el campo simplemente se ignorara." },
      { text: "4. No es necesario limpiar la base de datos para hacer rollback." },
    ], GREEN_LIGHT, GREEN),
    new Paragraph({ children: [new PageBreak()] })
  ];
}

// ── Section 4: Historial ─────────────────────────────────────────
function section4() {
  return [
    heading1("4. Historial de Cambios"),
    hr(),
    twoColTable([
      ["Version", "1.0"],
      ["Fecha", "12 de marzo de 2026"],
      ["Autor", "Equipo de Ingenieria MitoBooks"],
      ["Descripcion", "Creacion del documento. Implementacion verificada con 81 tests en verde."],
      ["Estado", "COMPLETADO"],
    ], BLUE_MID, ["Campo", "Detalle"]),
  ];
}

// ── Document assembly ────────────────────────────────────────────
const doc = new Document({
  numbering: {
    config: [{
      reference: "bullets",
      levels: [{
        level: 0, format: LevelFormat.BULLET, text: "\u2022", alignment: AlignmentType.LEFT,
        style: { paragraph: { indent: { left: 720, hanging: 360 } } }
      }]
    }]
  },
  styles: {
    default: {
      document: { run: { font: "Arial", size: 22 } }
    },
    paragraphStyles: [
      { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 32, bold: true, font: "Arial", color: BLUE_DARK },
        paragraph: { spacing: { before: 360, after: 160 }, outlineLevel: 0 } },
      { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 26, bold: true, font: "Arial", color: BLUE_MID },
        paragraph: { spacing: { before: 240, after: 120 }, outlineLevel: 1 } },
      { id: "Heading3", name: "Heading 3", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 22, bold: true, font: "Arial", color: BLUE_DARK },
        paragraph: { spacing: { before: 160, after: 80 }, outlineLevel: 2 } },
    ]
  },
  sections: [{
    properties: {
      page: {
        size: { width: 12240, height: 15840 },
        margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 }
      }
    },
    headers: {
      default: new Header({
        children: [new Paragraph({
          alignment: AlignmentType.RIGHT,
          border: { bottom: { style: BorderStyle.SINGLE, size: 4, color: BLUE_MID, space: 1 } },
          spacing: { after: 120 },
          children: [
            new TextRun({ text: "MitoBooks  |  Registro de Cambio: Campo Edad del Cliente", font: "Arial", size: 18, color: "666666" })
          ]
        })]
      })
    },
    footers: {
      default: new Footer({
        children: [new Paragraph({
          alignment: AlignmentType.CENTER,
          border: { top: { style: BorderStyle.SINGLE, size: 4, color: BLUE_MID, space: 1 } },
          spacing: { before: 120 },
          children: [
            new TextRun({ text: "Pagina ", font: "Arial", size: 18, color: "666666" }),
            new TextRun({ children: [PageNumber.CURRENT], font: "Arial", size: 18, color: "666666" }),
            new TextRun({ text: " de ", font: "Arial", size: 18, color: "666666" }),
            new TextRun({ children: [PageNumber.TOTAL_PAGES], font: "Arial", size: 18, color: "666666" }),
            new TextRun({ text: "  |  Confidencial - Uso interno", font: "Arial", size: 18, color: "999999" }),
          ]
        })]
      })
    },
    children: [
      ...coverPage(),
      ...section1(),
      ...section2(),
      ...section3(),
      ...section4(),
    ]
  }]
});

Packer.toBuffer(doc).then(buffer => {
  fs.writeFileSync("doc/cambio-campo-edad-cliente.docx", buffer);
  console.log("Documento generado: generate/doc/cambio-campo-edad-cliente.docx");
});
