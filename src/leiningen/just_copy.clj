(ns leiningen.just-copy
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [org.satta.glob :as g]))


(defn process-path [path project]
  (println path)
  (cond
    (string? path)  path
    (keyword? path) (project path)
    (vector? path) (str/join (java.io.File/separator) (map #(process-path % project) path))))

(defn directory? [f]
  (.isDirectory f))

(defn copy-files-to-dest [files dest]
  (doseq [s files]
    (let [dest-file (io/as-file (str dest (java.io.File/separator) (.getName s)))]
      (if (directory? s)
        (copy-files-to-dest (rest (file-seq s)) dest-file)
        (do
          (io/make-parents dest-file)
          (io/copy s dest-file))))))

(defn just-copy
  "copies files around..."
  [project & args]
  (let [files (map (fn [[source dest]]
                     {:src (g/glob (process-path source project)) :dest (process-path dest project)})
                   (:just-copy project))]
    (doseq [{:keys [src dest]} files]
      (copy-files-to-dest src dest))))






