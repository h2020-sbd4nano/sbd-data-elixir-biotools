// Copyright (c) 2022  Egon Willighagen <egon.willighagen@gmail.com>
//
// GPL v3

@Grab(group='io.github.egonw.bacting', module='managers-rdf', version='0.3.3')
@Grab(group='io.github.egonw.bacting', module='managers-ui', version='0.3.3')

import groovy.json.JsonSlurper

bioclipse = new net.bioclipse.managers.BioclipseManager(".");
rdf = new net.bioclipse.managers.RDFManager(".");

licenseURImap = new HashMap()
licenseURImap.put("AGPL-3.0", "https://opensource.org/licenses/AGPL-3.0")
licenseURImap.put("Apache-2.0", "http://www.apache.org/licenses/LICENSE-2.0")
licenseURImap.put("BSD-2-Clause", "https://opensource.org/license/bsd-2-clause/")
licenseURImap.put("BSD-3-Clause", "https://opensource.org/license/bsd-3-clause/")
licenseURImap.put("CC-BY-3.0", "http://creativecommons.org/licenses/by/3.0/")
licenseURImap.put("CC-BY-4.0", "https://creativecommons.org/licenses/by/4.0/")
licenseURImap.put("CC-BY-NC-4.0", "https://creativecommons.org/licenses/by-nc/4.0/")
licenseURImap.put("CECILL-2.1", "https://opensource.org/licenses/CECILL-2.1")
licenseURImap.put("GPL-3.0", "https://opensource.org/licenses/GPL-3.0")
licenseURImap.put("MIT", "https://opensource.org/licenses/MIT")
licenseURImap.put("Not licensed", "https://www.wikidata.org/entity/Q50423863")
licenseURImap.put("Other", "http://example.com/unknown")
licenseURImap.put("Unlicense", "https://spdx.org/licenses/Unlicense")

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
  dct:license <${(tool.license != null) ? licenseURImap.get(tool.license) : "http://example.com/unknown"}> ;
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

