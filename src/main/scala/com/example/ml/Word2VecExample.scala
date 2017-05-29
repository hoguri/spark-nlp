package com.example.ml

import java.io.StringReader
import org.apache.lucene.analysis.ja.JapaneseTokenizer
import org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}
import scala.collection.mutable.ArrayBuffer

object Word2VecExample {
  def main(args: Array[String]) = {
    require(args.length >= 2, "Word2VecExample filePath word")
    
    val conf = new SparkConf
    val sc = new SparkContext(conf)
    try {
      val filePath = args(0)
      val input = sc.textFile(filePath).map(line => {
        val words = new ArrayBuffer[String]()
        val stream = new JapaneseTokenizer(null, true, JapaneseTokenizer.Mode.NORMAL)
        stream.setReader(new StringReader(line))
        val chAttr = stream.addAttribute(classOf[CharTermAttribute])
        val bfAttr = stream.addAttribute(classOf[BaseFormAttribute])
        val posAttr = stream.addAttribute(classOf[PartOfSpeechAttribute])
        stream.reset()
        while(stream.incrementToken()) {
          val ch = chAttr.toString
          val bf = bfAttr.getBaseForm()
          val pos = posAttr.getPartOfSpeech().split("-")(0)
          val sf = 
          (pos, bf) match {
            case ("名詞", _) | ("動詞", null) | ("形容詞", null) => words += ch
            case ("動詞", _) | ("形容詞", _)                      => words += bf
            case (_, _)                                           =>
          }
        }
        stream.close()
        words.toSeq
      })

      val word2vec = new Word2Vec()
      val model = word2vec.fit(input)
      val word = args(1)
      for((synonym, similarity) <- model.findSynonyms(word, 10)) {
        println(s"$synonym $similarity")
      }
    } finally {
      sc.stop()
    }
  }
}