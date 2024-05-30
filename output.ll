; ModuleID = 'module'
source_filename = "module"

define i32 @main() {
mainEntry:
  %x = alloca i32, align 4
  store i32 0, i32* %x, align 4
  %y = alloca i32, align 4
  store i32 0, i32* %y, align 4
  %z = alloca i32, align 4
  store i32 0, i32* %z, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  br label %whileCondition0

whileCondition0:                                  ; preds = %end04, %mainEntry
  %x1 = load i32, i32* %x, align 4
  %cmptmp = icmp sle i32 %x1, 2
  %zexttmp = zext i1 %cmptmp to i32
  %finalcmp = icmp ne i32 0, %zexttmp
  br i1 %finalcmp, label %whileBody0, label %end0

whileBody0:                                       ; preds = %whileCondition0
  br label %whileCondition02

end0:                                             ; preds = %whileCondition0
  %count15 = load i32, i32* %count, align 4
  ret i32 %count15

whileCondition02:                                 ; preds = %end012, %whileBody0
  %y5 = load i32, i32* %y, align 4
  %cmptmp6 = icmp sle i32 %y5, 2
  %zexttmp7 = zext i1 %cmptmp6 to i32
  %finalcmp8 = icmp ne i32 0, %zexttmp7
  br i1 %finalcmp8, label %whileBody03, label %end04

whileBody03:                                      ; preds = %whileCondition02
  %count9 = load i32, i32* %count, align 4
  %addtmp = add i32 %count9, 1
  store i32 %addtmp, i32* %count, align 4
  %y10 = load i32, i32* %y, align 4
  %addtmp11 = add i32 %y10, 1
  store i32 %addtmp11, i32* %y, align 4
  br i1 true, label %true0, label %false0

end04:                                            ; preds = %true0, %whileCondition02
  %x13 = load i32, i32* %x, align 4
  %addtmp14 = add i32 %x13, 1
  store i32 %addtmp14, i32* %x, align 4
  br label %whileCondition0

true0:                                            ; preds = %whileBody03
  br label %end04
  br label %end012

false0:                                           ; preds = %whileBody03
  br label %end012

end012:                                           ; preds = %false0, %true0
  br label %whileCondition02
}
