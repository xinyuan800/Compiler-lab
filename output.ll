; ModuleID = 'module'
source_filename = "module"

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 0, i32* %a, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  br label %while.begin0

while.begin0:                                     ; preds = %end06, %mainEntry
  %a1 = load i32, i32* %a, align 4
  %cmptmp = icmp sle i32 %a1, 0
  %zexttmp = zext i1 %cmptmp to i32
  %finalcmp = icmp ne i32 0, %zexttmp
  br i1 %finalcmp, label %true0, label %false0

true0:                                            ; preds = %while.begin0
  %a2 = load i32, i32* %a, align 4
  %subtmp = sub i32 %a2, 1
  store i32 %subtmp, i32* %a, align 4
  %count3 = load i32, i32* %count, align 4
  %addtmp = add i32 %count3, 1
  store i32 %addtmp, i32* %count, align 4
  %a7 = load i32, i32* %a, align 4
  %cmptmp8 = icmp slt i32 %a7, -20
  %zexttmp9 = zext i1 %cmptmp8 to i32
  %finalcmp10 = icmp ne i32 0, %zexttmp9
  br i1 %finalcmp10, label %true04, label %false05

false0:                                           ; preds = %true04, %while.begin0
  br label %end0

end0:                                             ; preds = %false0
  %count11 = load i32, i32* %count, align 4
  ret i32 %count11

true04:                                           ; preds = %true0
  br label %false0
  br label %end06

false05:                                          ; preds = %true0
  br label %end06

end06:                                            ; preds = %false05, %true04
  br label %while.begin0
}
