# Esri ArcGIS open data API

[Esri] provides geographical core datasets openly under the umbrella of the
[ArcGIS] project. This project provides a solution to utilize address datasets
from ArcGIS and provide a simple REST API for select views on the data.

esri-api is [OpenAPI 2.0] compatible and integrates [Swagger UI] for API
exploration and direct experimentation.

[Esri]: https://www.esri.com/en-us/home
[ArcGIS]: https://www.esri.com/en-us/arcgis/about-arcgis/overview
[OpenAPI 2.0]: https://swagger.io/resources/open-api/
[Swagger UI]: https://swagger.io/tools/swagger-ui/

## Building

esri-api can be run both directly using [clj] or as a uberjar. To build the uberjar, run

    make uberjar

This requires having `curl` installed to fetch the dataset and a recent version
of Clojure with [clj] on path, as well as a compatible JDK version.

[clj]: https://clojure.org/guides/deps_and_cli

## Usage

    java -jar target/esri-api.jar

    Esri ArcGIS open data API

    USAGE:
        esri-api [options]

    OPTIONS:
      -p, --port PORT  3000  Port number
      -h, --help

After starting, point your web browser at http://localhost:3000 to open Swagger
UI and obtain API documentation.

## Copyright

Copyright 2020 Alexander Dorn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
