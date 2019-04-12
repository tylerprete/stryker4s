package stryker4s.testutil

import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import stryker4s.files.DiskFileIO

object MutationTestingElementsJsonSchema {

  private val schemaLocation = "mutation-testing-report-schema/mutation-testing-report-schema.json"

  /** Load json schema from resources so we can validate our report case classes output to the json schema.
    */
  def mutationTestingElementsJsonSchema: Schema = {
    val schema = DiskFileIO.readResource(schemaLocation).mkString
    SchemaLoader
      .builder()
      .schemaJson(new JSONObject(schema))
      .build()
      .load()
      .build()
  }

}
