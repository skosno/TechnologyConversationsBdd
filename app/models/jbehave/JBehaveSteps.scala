package models.jbehave

import java.io.File
import org.clapper.classutil.ClassFinder
import play.api.libs.json.{Json, JsValue}
import org.jbehave.core.steps.{StepCandidate, Steps}
import org.jbehave.core.configuration.MostUsefulConfiguration
import scala.collection.JavaConversions._
import com.technologyconversations.bdd.steps.util.{BddOptionParam, BddParam}
import org.jbehave.core.annotations.{Then, When, Given}
import groovy.lang.GroovyClassLoader

class JBehaveSteps(stepsDir: String = "steps", composites: List[String] = List.empty[String]) {

  def stepsToJson: JsValue = {
    val stepsMap = stepsCandidates.map { step =>
      val stepString = step.toString
      val stepType = stepString.split(" ")(0)
      Map(
        "type" -> stepType,
        "step" -> stepString.replaceFirst("GIVEN", "Given").replaceFirst("WHEN", "When").replaceFirst("THEN", "Then")
      )
    }
    Json.toJson(Map("steps" -> Json.toJson(stepsMap)))
  }

  def classesToJson: JsValue = {
    val classesMap = classes.map { className =>
      Map(
        "name" -> Json.toJson(className.substring(className.lastIndexOf(".") + 1)),
        "fullName" -> Json.toJson(className),
        "params" -> Json.toJson(classParamsMap(className))
      )
    }
    Json.toJson(Map("classes" -> Json.toJson(classesMap)))
  }

  private[jbehave] def stepsJars = {
    val classPathJars = ClassFinder.classpath.filter(_.getName.toLowerCase.contains("steps")).toList
    val stepsJars = new java.io.File(stepsDir).listFiles.toList
    classPathJars ::: stepsJars
  }

  private[jbehave] def classes = {
    val jarClasses = ClassFinder(stepsJars).getClasses()
      .filter(classInfo => classInfo.name.toLowerCase.contains("steps"))
      .filter(classInfo => hasSteps(classInfo.name))
      .map(_.name)
      .toList.distinct
    val compositeClasses = composites.map { composite =>
      if (composite.endsWith(".java")) {
        composite.replace(".java", "").replace(File.separator, ".")
      } else {
        composite
      }
    }
    jarClasses ::: compositeClasses
  }

  private[jbehave] def hasSteps(className: String): Boolean = {
    for(method <- Class.forName(className).getMethods) {
      if (method.getAnnotation(classOf[Given]) != null ||
          method.getAnnotation(classOf[When]) != null ||
          method.getAnnotation(classOf[Then]) != null) return true
    }
    false
  }

  private[jbehave] def classParams(className: String): List[BddParam] = {
    Class.forName(className).getMethods
      .filter(_.getAnnotation(classOf[BddParam]) != null)
      .map(_.getAnnotation(classOf[BddParam]))
      .toList
  }

  private[jbehave] def classParamsMap(className: String): List[Map[String, JsValue]] = {
    classParams(className).map(param => Map(
      "key" -> Json.toJson(param.value()),
      "value" -> Json.toJson(System.getProperty(className + "." + param.value(), "")),
      "description" -> Json.toJson(param.description()),
      "options" -> Json.toJson(optionsToJson(param.options().toList))
    ))
  }

  private[jbehave] def optionsToJson(options: List[BddOptionParam]): List[Map[String, JsValue]] = {
    options.map(option => Map(
      "text" -> Json.toJson(option.text()),
      "value" -> Json.toJson(option.value()),
      "isSelected" -> Json.toJson(option.isSelected())
    ))
  }



  private[jbehave] def steps = {
    val config = new MostUsefulConfiguration()
    classes.map { className =>
      if (className.endsWith(".groovy")) {
        val instance = new GroovyClassLoader().parseClass(new File(className)).newInstance
        new Steps(config, instance)
      } else {
        val instance = Class.forName(className).newInstance()
        new Steps(config, instance)
      }
    }
  }

  private[jbehave] def stepsCandidates = {
    def stepsCandidates(steps: List[Steps], candidates: List[StepCandidate]): List[StepCandidate] = {
      if (steps.isEmpty) {
        candidates
      } else {
        stepsCandidates(steps.tail, candidates ::: steps.head.listCandidates().toList)
      }
    }
    stepsCandidates(steps, List()).sortWith(_.toString < _.toString)


  }

}

object JBehaveSteps {

  def apply(): JBehaveSteps = new JBehaveSteps
  def apply(stepsDir: String = "steps", composites: List[String] = List.empty[String]): JBehaveSteps = new JBehaveSteps(stepsDir, composites)

}
