# Kafka bindings for Azure Functions

This is a sample project that illustrates how to use Kafka bindings for Azure Functions with Java (using Spring Cloud Functions).

This repository includes an ARM template that provisions a number of Azure resources to demonstrate the capability. The template will create an Event Hub with Kafka endpoints, an Application Insights instance for monitoring the application, a Storage Account and a Function App on the Elastic Premium Plan.

The Function App has two functions, a `producer` that sends every minute a simple message, and a `receiver` that consumes those messages, through Kafka APIs and Azure Functions Kafka bindings. For more information on Azure Functions Kafka bindings, see the [docs](https://github.com/Azure/azure-functions-kafka-extension).

## Setup

Assuming that the Azure CLI and Maven are installed, the following snippets can be used to provision and configure the required resources.

```bash
RG=...  # the target resource group, assuming that this has been created already
BASE_NAME=...  # i.e. kafka, choose something with less than 6 alphanumeric characters
FUNC_APP_NAME=`az deployment group create -g $RG \
    --template-file azuredeploy.json \
    --parameters baseName="$BASE_NAME" \
    --query properties.outputs.functionAppName.value \
    -o tsv`
```

> As some of the Azure resources need to have globally unique names, the included ARM templates attempt to
> generate more or less unique names by appending a hash of the resource group name to the provided base name
> If you prefer to have more control or need to use specific names, just update the variables in the templates.
> At the moment the topic name is hard-coded for the `producer` as the current Kafka bindings library
> currently cannot handle environment variables (see issue #[158](https://github.com/Azure/azure-functions-kafka-extension/issues/158)).

If you'd like to experiment with the scaling capabilities of Azure Functions you need to turn that on explicitly by running the following command:

```bash
az resource update -g $RG -n $FUNC_APP_NAME/config/web \
  --resource-type Microsoft.Web/sites \
  --set properties.functionsRuntimeScaleMonitoringEnabled=1 \
  -o none
```

## Development

Before you can run the code locally, you need to make sure that the native libraries for the Azure Functions extensions are initialized. The following Maven command takes care of that through the [Azure Functions Maven Plugin](https://github.com/microsoft/azure-maven-plugins/tree/master/azure-functions-maven-plugin)

```bash
mvn package -DfunctionAppName=$FUNC_APP_NAME
```

You can now use either Maven or your favourite IDE to run/debug your code, just make sure that you've configured your `local.settings.json` properly and started your local [Azure Storage Emulator](https://docs.microsoft.com/en-us/azure/storage/common/storage-use-emulator). See below for an example local settings file where Event Hub is used as the Kafka endpoint.

```json
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "UseDevelopmentStorage=true",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "BROKERS": "<event hub namespace>.servicebus.windows.net:9093",
    "KAFKA_TOPIC": "<event hub name>",
    "KAFKA_USER": "$ConnectionString",
    "KAFKA_PASSWORD": "<event hub SAS key>"
  }
}
```

For more information on how to run Azure Functions in Java locally have a look at the [docs](https://docs.microsoft.com/en-us/azure/azure-functions/functions-reference-java?tabs=consumption).

## Deployment

First, run the following command to build the project:

```bash
mvn clean package -DfunctionAppName=$FUNC_APP_NAME
```

Now you've got multiple ways of deploying the application; in this example we'll use the deploy capabilities of the Azure Functions Maven plugin, but alternatively we could've packaged the binaries in a zip file and do a zip deployment through [Azure CLI](https://docs.microsoft.com/en-us/azure/azure-functions/deployment-zip-push#cli).

```bash
mvn azure-functions:deploy -DresourceGroupName=$RG -DfunctionAppName=$FUNCTION_APP_NAME
```

That's it, you can monitor the behaviour through the Azure Portal and/or Application Insights.
