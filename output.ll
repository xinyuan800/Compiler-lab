; ModuleID = 'module'
source_filename = "module"

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 0, i32* %a, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  br label %whileCondition0

whileCondition0:                                  ; preds = %end015, %mainEntry
  %a1 = load i32, i32* %a, align 4
  %cmptmp = icmp sle i32 %a1, 0
  %zexttmp = zext i1 %cmptmp to i32
  %finalcmp = icmp ne i32 0, %zexttmp
  br i1 %finalcmp, label %whileBody0, label %end0

whileBody0:                                       ; preds = %whileCondition0
  br label %whileCondition02

end0:                                             ; preds = %true0, %whileCondition0
  %count20 = load i32, i32* %count, align 4
  ret i32 %count20

whileCondition02:                                 ; preds = %whileBody03, %whileBody0
  %a5 = load i32, i32* %a, align 4
  %cmptmp6 = icmp slt i32 %a5, -10
  %zexttmp7 = zext i1 %cmptmp6 to i32
  %finalcmp8 = icmp ne i32 0, %zexttmp7
  br i1 %finalcmp8, label %whileBody03, label %end04

whileBody03:                                      ; preds = %whileCondition02
  %count9 = load i32, i32* %count, align 4
  %addtmp = add i32 %count9, 1
  store i32 %addtmp, i32* %count, align 4
  %a10 = load i32, i32* %a, align 4
  %subtmp = sub i32 %a10, 2
  store i32 %subtmp, i32* %a, align 4
  br label %whileCondition02

end04:                                            ; preds = %whileCondition02
  %a11 = load i32, i32* %a, align 4
  %subtmp12 = sub i32 %a11, 1
  store i32 %subtmp12, i32* %a, align 4
  %count13 = load i32, i32* %count, align 4
  %addtmp14 = add i32 %count13, 1
  store i32 %addtmp14, i32* %count, align 4
  %a16 = load i32, i32* %a, align 4
  %cmptmp17 = icmp slt i32 %a16, -20
  %zexttmp18 = zext i1 %cmptmp17 to i32
  %finalcmp19 = icmp ne i32 0, %zexttmp18
  br i1 %finalcmp19, label %true0, label %false0

true0:                                            ; preds = %end04
  br label %end0
  br label %end015

false0:                                           ; preds = %end04
  br label %end015

end015:                                           ; preds = %false0, %true0
  br label %whileCondition0
}
