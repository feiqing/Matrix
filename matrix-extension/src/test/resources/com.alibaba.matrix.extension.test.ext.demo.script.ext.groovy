static def apply(arg1, arg2, arg3) {
    println "arg1 = ${arg1}"
    println "arg2: "
    println arg2.name
    println arg2.list[0]
    println arg2.map["key"]
    println "arg3 = ${arg3}"

    def map = new HashMap()
    map.put("result", arg2.name)

    println "map = ${map}"

    return "SUCCESS"
}