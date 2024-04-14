(ns project.core-spec)

(js/test "Test example"
         (fn []
           (-> (js/expect 2)
               (.-not)
               (.toBe 1))))