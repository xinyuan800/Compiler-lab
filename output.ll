; ModuleID = 'module'
source_filename = "module"

define i32 @factorial(i32 %0) {
factorialEntry:
  %n = alloca i32, align 4
  store i32 %0, i32* %n, align 4
  %result = alloca i32, align 4
  store i32 0, i32* %result, align 4
  %i = alloca i32, align 4
  store i32 1, i32* %i, align 4
  br label %whileCondition0

whileCondition0:                                  ; preds = %end014, %factorialEntry
  %i1 = load i32, i32* %i, align 4
  %n2 = load i32, i32* %n, align 4
  %cmptmp = icmp sle i32 %i1, %n2
  %zexttmp = zext i1 %cmptmp to i32
  %finalcmp = icmp ne i32 0, %zexttmp
  br i1 %finalcmp, label %whileBody0, label %end0

whileBody0:                                       ; preds = %whileCondition0
  %j = alloca i32, align 4
  %i3 = load i32, i32* %i, align 4
  store i32 %i3, i32* %j, align 4
  %temp = alloca i32, align 4
  store i32 1, i32* %temp, align 4
  br label %whileCondition04

end0:                                             ; preds = %true0, %whileCondition0
  %result23 = load i32, i32* %result, align 4
  ret i32 %result23

whileCondition04:                                 ; preds = %whileBody05, %whileBody0
  %j7 = load i32, i32* %j, align 4
  %cmptmp8 = icmp sgt i32 %j7, 0
  %zexttmp9 = zext i1 %cmptmp8 to i32
  %finalcmp10 = icmp ne i32 0, %zexttmp9
  br i1 %finalcmp10, label %whileBody05, label %end06

whileBody05:                                      ; preds = %whileCondition04
  %temp11 = load i32, i32* %temp, align 4
  %j12 = load i32, i32* %j, align 4
  %multmp = mul i32 %temp11, %j12
  store i32 %multmp, i32* %temp, align 4
  %j13 = load i32, i32* %j, align 4
  %subtmp = sub i32 %j13, 1
  store i32 %subtmp, i32* %j, align 4
  br label %whileCondition04

end06:                                            ; preds = %whileCondition04
  %temp15 = load i32, i32* %temp, align 4
  %cmptmp16 = icmp eq i32 %temp15, 2
  %zexttmp17 = zext i1 %cmptmp16 to i32
  %finalcmp18 = icmp ne i32 0, %zexttmp17
  br i1 %finalcmp18, label %true0, label %false0

true0:                                            ; preds = %end06
  br label %end0
  br label %end014

false0:                                           ; preds = %end06
  br label %end014

end014:                                           ; preds = %false0, %true0
  %result19 = load i32, i32* %result, align 4
  %temp20 = load i32, i32* %temp, align 4
  %addtmp = add i32 %result19, %temp20
  store i32 %addtmp, i32* %result, align 4
  %i21 = load i32, i32* %i, align 4
  %addtmp22 = add i32 %i21, 1
  store i32 %addtmp22, i32* %i, align 4
  br label %whileCondition0
}

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 5, i32* %a, align 4
  %a1 = load i32, i32* %a, align 4
  %returnValue = call i32 @factorial(i32 %a1)
  ret i32 %returnValue
}
