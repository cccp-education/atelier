package workspace.ai

import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import org.gradle.api.Plugin
import org.gradle.api.Project
import workspace.ai.AssistantManager.apiKey
import java.time.Duration
import java.time.Duration.ofSeconds


class AssistantPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.run {
            task("helloAssistant") {
                group = "school-ai"
                description = "Greetings from AI assistant!"
                doFirst { println("Hello from assistant") }
            }

            task("displayOpenAIKey") {
                group = "school-ai"
                description = "Display the open ai api keys stored in private.properties"
                doFirst { println("apiKey : ${project.apiKey}") }
            }

            task("helloOllama") {
                group = "school-ai"
                description = "Display the open ai chatgpt hello prompt request."
                doFirst {
                    OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName("phi3")
                        .temperature(0.8)
                        .timeout(ofSeconds(6000))
                        .build()
                        .run {
                            println(
                                generate(
                                    """config```--lang=fr```. 
                            |Salut, je te baptise E3P0 tu es mon assistant,
                            | je suis développeur logiciel dans l'EdTech et formateur.
                            | ta mission est de m'aider dans mon activité. répon moi en maximum 350 mots""".trimMargin()
                                )
                            )
                        }
                }
                doLast{
                    /*import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CompletableFuture;

public class OllamaStreamingChatExample {

  static String MODEL_NAME = "orca-mini"; // try "mistral", "llama2", "codellama" or "phi"
  static String DOCKER_IMAGE_NAME = "langchain4j/ollama-" + MODEL_NAME + ":latest";

  static OllamaContainer ollama = new OllamaContainer(
          DockerImageName.parse(DOCKER_IMAGE_NAME).asCompatibleSubstituteFor("ollama/ollama"));

  public static void main(String[] args) {
    ollama.start();
    StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
        .baseUrl(String.format("http://%s:%d", ollama.getHost(), ollama.getMappedPort(PORT)))
        .modelName(MODEL_NAME)
        .temperature(0.0)
        .build();

    String userMessage = "Write a 100-word poem about Java and AI";

    CompletableFuture<Response<AiMessage>> futureResponse = new CompletableFuture<>();
    model.generate(userMessage, new StreamingResponseHandler<AiMessage>() {

      @Override
      public void onNext(String token) {
        System.out.print(token);
      }

      @Override
      public void onComplete(Response<AiMessage> response) {
        futureResponse.complete(response);
      }

      @Override
      public void onError(Throwable error) {
        futureResponse.completeExceptionally(error);
      }
    });

    futureResponse.join();
    ollama.stop();
  }
}*/
                }
            }

            task("helloOpenAi") {
                group = "school-ai"
                description = "Display the open ai chatgpt hello prompt request."
                doFirst {
                    OpenAiChatModel
                        .withApiKey(apiKey)
                        .generate("Say 'Hello World'")
                        .apply(::println)
                }
            }
        }
    }
}
