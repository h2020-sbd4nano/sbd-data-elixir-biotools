// Copyright (c) 2022  Egon Willighagen <egon.willighagen@gmail.com>
//
// GPL v3

@Grab(group='io.github.egonw.bacting', module='managers-rdf', version='0.3.3')
@Grab(group='io.github.egonw.bacting', module='managers-ui', version='0.3.3')

import groovy.json.JsonSlurper

bioclipse = new net.bioclipse.managers.BioclipseManager(".");
rdf = new net.bioclipse.managers.RDFManager(".");

def nextPage(callURL, page) {
  fullCall = (page != null) ? callURL + page : callURL
  jsonContent = bioclipse.download(fullCall, "application/json")
  def biotoolsData = new JsonSlurper().parseText(jsonContent) 

  for (tool in biotoolsData.list) {
    println """
<https://bio.tools/${tool.biotoolsCURIE}> a sbd:Resource ;
  dc:source <https://bio.tools/t?domain=toxicology> ;
  rdfs:label "${tool.name}" ;
  dc:description "${tool.description}"@en ;
  dct:license <${(tool.license != null) ? tool.license : "http://example.com/unknown"}> ;
  foaf:page <${tool.homepage}> .
  """
  }
  if (biotoolsData.next) nextPage(callURL, "&" + biotoolsData.next.substring(1))
}

println "@prefix dc:    <http://purl.org/dc/elements/1.1/> ."
println "@prefix dct:   <http://purl.org/dc/terms/> ."
println "@prefix foaf:  <http://xmlns.com/foaf/0.1/> ."
println "@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> ."
println "@prefix sbd:   <https://www.sbd4nano.eu/rdf/#> ."
println "@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> ."
println "@prefix void:  <http://rdfs.org/ns/void#> ."

println ""
println "<https://bio.tools/t?domain=toxicology>"
println " a                    void:DatasetDescription ;"
println " dc:source            <https://bio.tools/t?domain=toxicology> ;"
println " dct:title            \"The bio.tools Toxicology Collection\"@en ;"
println " foaf:img             <https://bio.tools/img/elixir_biotools_transparent.png> ;"
println " dct:license          <https://creativecommons.org/licenses/by/4.0/> . # license of this metadata"
println ""

nextPage("https://bio.tools/api/t/?topicID=%22topic_2840%22", null)

