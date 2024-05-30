; ModuleID = 'module'
source_filename = "module"

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 0, i32* %a, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  %a1 = load i32, i32* %a, align 4
  %addtmp = add i32 %a1, 1
  %lhs = icmp ne i32 0, %addtmp
  %result = alloca i1, align 1
  br i1 %lhs, label %or.true0, label %or.false0

true0:                                            ; preds = %or.end0
  %a6 = load i32, i32* %a, align 4
  ret i32 %a6
  br label %end0

false0:                                           ; preds = %or.end0
  br label %end0

end0:                                             ; preds = %false0, %true0
  %count7 = load i32, i32* %count, align 4
  ret i32 %count7

or.true0:                                         ; preds = %mainEntry
  store i1 true, i1* %result, align 1
  br label %or.end0

or.false0:                                        ; preds = %mainEntry
  %count2 = load i32, i32* %count, align 4
  %a3 = load i32, i32* %a, align 4
  %subtmp = sub i32 %count2, %a3
  %lhs4 = icmp ne i32 0, %subtmp
  store i1 %lhs4, i1* %result, align 1
  br label %or.end0

or.end0:                                          ; preds = %or.false0, %or.true0
  %result5 = load i1, i1* %result, align 1
  %cond = icmp ne i32 0, i1 %result5
  br i1 %cond, label %true0, label %false0
}
