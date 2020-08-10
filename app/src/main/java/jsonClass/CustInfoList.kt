class CustInfoList(
    custid: String?,
    cust_name: String?,
    cust_kana: String?,
    point_ido: String?,
    point_keido: String?,
    tel: String?,
    addres: String?
) {
    var custid: String? = null
    var cust_name: String? = null
    var cust_kana: String? = null
    var point_ido: String? = null
    var point_keido: String? = null
    var tel: String? = null
    var addres: String? = null

    init {
        this.custid
        this.cust_name
        this.cust_kana
        this.point_ido
        this.point_keido
        this.tel
        this.addres
    }
}