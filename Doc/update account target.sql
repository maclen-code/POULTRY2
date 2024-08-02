--update x
--set rid=t.Rid
--from tbl_Bmeg_Target_Act x 
--left join (select acctNo,max(rid) Rid from tbl_CustAcctDSP x group by x.AcctNo) t
--on t.AcctNo=x.AcctNo


